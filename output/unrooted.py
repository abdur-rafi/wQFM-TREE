'''
    This module defines some handy operations on unrooted trees.
'''

###########################################################################
##    Copyright 2010 Rahul Suri and Tandy Warnow.
##    This file is part of spruce.
##
##    spruce is free software: you can redistribute it and/or modify
##    it under the terms of the GNU General Public License as published by
##    the Free Software Foundation, either version 3 of the License, or
##    (at your option) any later version.
##
##    spruce is distributed in the hope that it will be useful,
##    but WITHOUT ANY WARRANTY; without even the implied warranty of
##    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
##    GNU General Public License for more details.
##
##    You should have received a copy of the GNU General Public License
##    along with spruce.  If not, see <http://www.gnu.org/licenses/>.
###########################################################################

import copy
from newick_modified.tree import *

def readNewickFile(file):
    '''Read a tree from file, augment it with degree info.'''
    f = open(file)
    data = f.read()
    f.close()

    if data.strip().endswith(':0.0;'):
        (data, _, _) = data.strip().rpartition(':0.0;')

    tree = parse_tree(data)
    tree = addDegreeInfo(tree)
    return (tree)



def readMultipleTreesFromFile(file):
    '''Read multiple semicolon-terminated trees from a file.'''
    f = open(file)
    sourceTrees = readMultipleTreesFromStream(f)
    f.close()

    return (sourceTrees)



def readMultipleTreesFromStream(stream):
    '''Read multiple semicolon-terminated trees from a stream.'''
    data = stream.read()

    sources = data.strip().split(';')
    if sources[-1] == '':
        sources = sources[:-1]

    sourceTrees = []

    for source in sources:
        if source.endswith(':0.0'):
            (source, _, _) = source.rpartition(':0.0')

        sourceTrees.append(source)
    return (sourceTrees)



def isNonLeaf(tree):
    '''Return true iff the given node is an internal node.'''
    return (hasattr(tree, "get_edges"))



def addDegreeInfo(tree):
    '''Augment tree data structure with degree information.'''

    class DegreeComputer(TreeVisitor):
        '''Compute degree for each node, except the root.'''
        def pre_visit_edge(self, src, bootstrap, length, dest):
            if isNonLeaf(dest):
                dest.degree = len(dest.get_edges()) + 1
            else:
                dest.degree = 1

    if (isNonLeaf(tree)):
        tree.dfs_traverse(DegreeComputer()) # case: non-root nodes
        tree.degree = len(tree.get_edges()) # case: root node
    else:
        tree.degree = 0                     # case: single-node tree

    return (tree)



def xfindPolytomies(tree):
    '''
        Find all polytomies in a tree.  Note: addDegreeInfo() *must* be called 
        prior to this function's invocation.
    '''
    if (tree.degree > 3):
        yield tree

    # recursive subtree traversal
    if isNonLeaf(tree):
        for edge in tree.get_edges():
            for p in xfindPolytomies(edge[0]):
                yield p



def getInternalEdges (tree):
    '''Get internal edges as (src, dest) pairs from a tree.'''

    class InternalEdgeFinder(TreeVisitor):
        '''Create a list of edges which lead to non-leaf nodes.'''

        def __init__(self):
            self.internalEdges = []

        def pre_visit_edge(self, src, bootstrap, length, dest):
            if isNonLeaf(dest):
                self.internalEdges.append((src, dest))

        def getInternalEdges(self):
            return (self.internalEdges)

    # cases: null tree, singleton tree
    if (tree == None or not isNonLeaf(tree)):
        return []

    rootEdges = tree.get_edges()
    internalEdges = [] # return value
    cladesToVisit = [] # subtrees to search for internal edges

    if (len(rootEdges) >= 3):
        cladesToVisit.append(tree)

    elif (len(rootEdges) == 2):
        # if the root has degree 2, treat its pair of edges as a single internal
        #   edge, since we really care about unrooted trees
        internalRootEdges = [edge for edge in rootEdges if isNonLeaf(edge[0])]
        if (len(internalRootEdges) == 2):
            internalEdges.append((tree, internalRootEdges[0][0]))

        cladesToVisit.append(rootEdges[0][0])
        cladesToVisit.append(rootEdges[1][0])

    elif (len(rootEdges) == 1):
        cladesToVisit.append(rootEdges[0][0])

    else: # len(rootEdges) == 0
        return []

    for clade in cladesToVisit:
        visitor = InternalEdgeFinder()
        clade.dfs_traverse(visitor)
        internalEdges.extend(visitor.getInternalEdges())

    return (internalEdges)



# TODO: extend to non-binary trees
def xfindDistinguishingQtrees (tree, src, dest):
    '''
        Find all quartet trees distinguishing an internal edge in a tree.
        *Note*: currently works only for binary trees.
    '''

    # find leaf labels appearing on one side of the input edge
    destSets = [edge[0].get_leaves_identifiers() for edge in dest.get_edges()]

    # find leaf labels appearing on the other side of the input edge
    add_parent_links(tree)
    srcSets = [edge[0].get_leaves_identifiers() for edge in src.get_edges() if edge[0] != dest]
    if hasattr(src, "parent"):
        srcSets.append([])
        srcSiblings = [edge[0] for edge in src.parent.get_edges() if edge[0] != src]
        for sibling in srcSiblings:
            srcSets[-1].extend(sibling.get_leaves_identifiers())
    else: # case: src is the root, and the root is binary
        if len(srcSets) == 1:
            srcSets = [edge2[0].get_leaves_identifiers() for edge in src.get_edges()
                                                         for edge2 in edge[0].get_edges()
                                                         if edge[0] != dest]

    # assert: len(destSets) == 2
    # assert: len(srcSets) == 2

    # the 4 sets of leaf labels "split" by the input edge and the edges incident
    #   on its endpoints
    setA = destSets[0]
    setB = destSets[1]
    setC = srcSets[0]
    setD = srcSets[1]

    # assert: sets A-D pairwise disjoint
    # assert: len(setA)*len(setB)*len(setC)*len(setD) == # of values yielded

    # return elements "in" {setA x setB x setC x setD}
    for i in xrange(len(setA)):
        for j in xrange(len(setB)):
            for k in xrange(len(setC)):
                for l in xrange(len(setD)):
                    yield createQuartetTreeString(setA[i], setB[j], setC[k], setD[l])



def xfindDisplayedQtrees (tree, src, dest):
    ''' Find all quartet trees displayed by a tree across one of its internal edges.'''

    bp = findBipartition(tree, src, dest)
    if bp == None:
        return

    (setA, setB) = (bp[0], bp[1])
    (lenA, lenB) = (len(setA), len(setB))

    for i in xrange(0, lenA - 1):
        for j in xrange(i + 1, lenA):
            for k in xrange(0, lenB - 1):
                for l in xrange(k + 1, lenB):
                    yield createQuartetTreeString(setA[i], setA[j], setB[k], setB[l])



def findDisplayedQtrees (tree):
    '''Find all quartet trees displayed by a tree.'''

    qtrees = set()
    for (src, dest) in getInternalEdges(tree):
        qtrees |= set([qtree for qtree in xfindDisplayedQtrees(tree, src, dest)])

    # sort & return
    qtrees = tuple(sorted(list(qtrees)))
    return (qtrees)



def createQuartetTreeString (i, j, k, l):
    '''Join sibling leaves with a comma, pairs of those with a vertical bar.'''
    quartet = [[repr(i),repr(j)], [repr(k), repr(l)]]
    quartet[0].sort()
    quartet[1].sort()
    quartet.sort()
    return '|'.join((','.join(quartet[0]),
                     ','.join(quartet[1])))



def findBipartition (tree, src, dest):
    '''Find the bipartition induced on a tree by a given edge.'''

    setA = set(dest.get_leaves_identifiers())
    setB = set(tree.get_leaves_identifiers()) - setA

    # weed out sneaky trivial bipartitions
    if len(setA) == 1 or len(setB) == 1:
        return (None)

    setA = tuple(sorted(list(setA)))
    setB = tuple(sorted(list(setB)))

    return(tuple(sorted([setA, setB])))



def xfindBipartitions (tree):
    '''Find all non-trivial bipartitions in a tree.'''

    for (src, dest) in getInternalEdges(tree):
        bp = findBipartition(tree, src, dest)

        if bp == None:
            continue
        else:
            yield bp



def restrict (tree, taxonSet):
    '''Return the restriction of a tree to a given taxon set.'''

    class LeafTrimmer(TreeVisitor):
        '''Remove all leaves not appearing in the given taxon set.'''

        def post_visit_edge(self, src, bootstrap, length, dest):
            src._leaves_cache = None

            if len(dest.get_leaves_identifiers()) == 0:                     # case: empty subtree
                index = src._edges.index((dest, bootstrap, length))
                src._edges = src._edges[:index] + src._edges[index+1:]
                src._leaves_cache = None

            elif (not isNonLeaf(dest) and dest.identifier not in taxonSet): # case: "invalid" taxon
                index = src._edges.index((dest, bootstrap, length))
                src._edges = src._edges[:index] + src._edges[index+1:]
                src._leaves_cache = None

    class ZeroEventTrimmer(TreeVisitor):
        '''Suppress all nodes of degree 2 (except the root, b/c that's cool).'''

        def post_visit_edge(self, src, bootstrap, length, dest):
            src._leaves_cache = None
            addDegreeInfo(src)
            if dest.degree == 2:
                index = src._edges.index((dest, bootstrap, length))
                src._edges = src._edges[:index] + src._edges[index+1:]
                src._edges.insert(index, (dest.get_edges()[0][0], None, None))
                src._leaves_cache = None

    def rootFinder(tree):
        '''Return the tree with root of degree 1 removed.'''

        tree._leaves_cache = None
        addDegreeInfo(tree)
        while tree.degree == 1 and isNonLeaf(tree._edges[0][0]):
            addDegreeInfo(tree)
            (dest, bootstrap, length) = tree._edges[0]
            tree._edges = []

            del tree

            tree = dest
            tree._leaves_cache = None

        return(tree)

    if (tree == None):          # case: null tree
        return Tree()
    elif not isNonLeaf(tree):   # case: singleton tree
        if tree.identifier in taxonSet:
            return copy.deepcopy(tree)
        else:
            return Tree()
    else:                       # case: "normal" tree
        tree = copy.deepcopy(tree)
        tree.dfs_traverse(LeafTrimmer())
        tree.dfs_traverse(ZeroEventTrimmer())
        tree = rootFinder(tree)
        return (tree)
