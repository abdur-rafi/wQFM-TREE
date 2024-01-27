#!/lusr/bin/perl -w

use strict;
use warnings;
use Getopt::Long;

use FindBin qw($Bin);
#use lib "$Bin/..";  # ei line ami block korsi
BEGIN {  # BEGIN means this will all happen at compile time
    package constants;

    $INC{'constants.pm'}++;     # tell `require` that the package is loaded
    #use base 'Exporter';        # setup package to export
    #our @EXPORT_OK = qw( PI );  # what to export

    #use constant PI => 3.14159; # define your constant
use constant PAUP => "/home/abdur-rafi/Academic/Thesis/E-WQFM/paup -n ";
}

use constants;
#use constant PAUP => "/projects/sate3/tools/bin/paup -n ";

sub printUsage {
    my $msg = "perl $0
	-i=<gene trees>
	-o=<output prefix>
";
    die $msg;
}

GetOptions(
	"i=s"=>\my $gt_file,
	"o=s"=>\my $output_prefix,
);

printUsage() unless(defined($gt_file));
printUsage() unless(defined($output_prefix));

print STDERR "warning: please limit length of filenames\n";

my $nexus_file = "$output_prefix.nexus";
my $str_file = "$output_prefix.strict";
my $maj_file = "$output_prefix.majority";
my $gre_file = "$output_prefix.greedy";
my $paup_file = "$output_prefix.paup";

write_paup_file($gt_file, $nexus_file, $paup_file, $str_file, $maj_file, $gre_file);
run_paup($paup_file);
my $str_file_newick = convert_nexus_to_newick($str_file);
my $maj_file_newick = convert_nexus_to_newick($maj_file);
my $gre_file_newick = convert_nexus_to_newick($gre_file);

print "output at:\n$str_file_newick\n$maj_file_newick\n$gre_file_newick\n";
print "done.\n";

sub write_paup_file {
 my ($gt_file, $nexus_file, $paup_file, $str_file, $maj_file, $gre_file) = @_;
 convert_newick_to_nexus($gt_file, $nexus_file);

 my @taxa_list;
 get_taxa_list($gt_file, \@taxa_list);
 my $numTaxa = scalar(@taxa_list);

 my $data = "
#NEXUS

begin taxa;
        dimensions ntax=$numTaxa;
        taxlabels " . join(" ", @taxa_list) . ";
end;

begin paup;
        set autoclose = yes warntree = no warnreset = no notifybeep = no monitor = yes taxlabels = full;
        set criterion = parsimony;
        set increase = auto;
        gettrees file = $nexus_file allblocks = yes warntree = no unrooted = yes;
        contree all / strict = yes treefile = $str_file replace;
        contree all / strict = no majrule = yes treefile = $maj_file replace;
        contree all / strict = no majrule = yes le50 = yes treefile = $gre_file replace;
end;

quit warntsave = no;
";

 open(OUT, ">", $paup_file) or die "can't open $paup_file: $!";
 print OUT $data;
 close(OUT);
}

sub run_paup {
 my ($paup_file) = @_;

 my $command = constants::PAUP . "$paup_file >/dev/null";
 system($command);
}

sub get_taxa_list {
 my ($gt_file, $taxa_list_ref) = @_;

 my $gt = `cat $gt_file`;

 $gt =~ s/:-?[0-9]+(\.[0-9]+)?([eE]-[0-9]+)?//g; #br
 $gt =~ s/[\(\),;\n]/ /g; #all non-space separators
 $gt =~ s/\[&U\]//g; #unrooted
 $gt =~ s/\s+/\n/g;
 $gt =~ s/^\n//;
 $gt =~ s/\n$//;

 my $tempfile = "$output_prefix.temp";
 open(TEMP, ">", $tempfile) or die "can't open $tempfile: $!";
 print TEMP $gt;
 close(TEMP);

 @{$taxa_list_ref} = split(/\n/, `cat $tempfile | sort | uniq`);

 system("rm $tempfile");
}

sub convert_newick_to_nexus {
 my ($tree, $nexus_file) = @_;

 my $nexus_file_contents = "
#NEXUS

Begin trees;
";
 my @gts = `cat $tree`;
 my $counter = 0;
 foreach my $gt (@gts) {
  if($gt !~ /\[&U\]/) {
   $gt = "[&U] " . $gt;
  }
  $nexus_file_contents .= "tree TREE$counter = $gt";
  $counter++;
 }

 $nexus_file_contents .= "End;";

 open(NEX, ">", $nexus_file) or die "can't open $nexus_file: $!";
 print NEX $nexus_file_contents;
 close(NEX);
}

sub convert_nexus_to_newick {
 my ($tree) = @_;
 my $newick_file = "$tree.tree";
 my $newick_tree;

 my $tree_contents = `cat $tree`;
 if($tree_contents =~ /tree .*? = \[&U\] (.*?;)/) {
  $newick_tree = $1;
 }
 else {
  die "can't find tree in $tree\n";
 }

 open(TREE, ">", $newick_file) or die "can't open $newick_file: $!";
 print TREE $newick_tree."\n";
 close(TREE);

 return $newick_file;
}
