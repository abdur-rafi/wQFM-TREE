#!/usr/bin/env python

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

from optparse import OptionParser
from spruce.unrooted import *
from spruce.metrics import *
from spruce.mrp import readTreesFromRatchet
from newick_modified import parse_tree

desc = '''
           This script prints the FP rate, FN rate, and Robinson-Foulds distance 
           of a pair of trees.
       '''

parser = OptionParser(usage = "usage: %prog [options]", description = desc)
parser.add_option("-t", "--true", dest = "true_tree", help = "read true tree from FILE", metavar = "FILE")
parser.add_option("-e", "--estimate", dest = "estimate_tree", help = "read estimate tree from FILE", metavar = "FILE")

(options, args) = parser.parse_args()
if (options.estimate_tree == None or options.true_tree == None):
    parser.error("values for both options must be specified\ntry running with the --help flag")

try:
    trueTree = readNewickFile(options.true_tree)
except:
    trueTree = parse_tree(readTreesFromRatchet(options.true_tree)[0])

try:
    estimateTree = readNewickFile(options.estimate_tree)
except:
    estimateTree = parse_tree(readTreesFromRatchet(options.estimate_tree)[0])

print(getFpFnRfRates(trueTree, estimateTree))
