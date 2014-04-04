=begin
go through the lines of the file.

If you find a line that matches
vitro:hiddenFromDisplayBelowRoleLevelAnnot
and followed by a line that matches 
<http://vitro.mannlib.cornell.edu/ns/vitro/role#public> ;
--where "public" may be any string
Insert 2 lines after the second one, replacing Display by Publish on the first, and duplicating the second.

If you find a line that contains 
vitro:hiddenFromDisplayBelowRoleLevelAnnot
and does not match it, or is not followed by a line that matches 
<http://vitro.mannlib.cornell.edu/ns/vitro/role#public> ;
Issue a warning and continue.

<http://vitro.mannlib.cornell.edu/ns/vitro/0.7#hiddenFromDisplayBelowRoleLevelAnnot>
        <http://vitro.mannlib.cornell.edu/ns/vitro/role#public> ;

=end

FILE_PATH = "/Users/jeb228/Documents/EclipseStuff/vivoWorkspace/vivoCornell/productMods/bjl23/ingest/weill/grants/submodels/vivo-tbox2.ttl"
DISPLAY_URI = "<http://vitro.mannlib.cornell.edu/ns/vitro/0.7#hiddenFromDisplayBelowRoleLevelAnnot>"

def read_the_file()
  @lines = File.readlines(FILE_PATH);
end

def scan_the_lines()
  @lines.each_index do |i|
    @line1 = @lines[i]
    @line2 = @lines[i+1]
    @index = i
    if linesContainMatch()
      replicateProperty()
    else
      checkForMismatch()
    end
  end
end

def linesContainMatch()
  return false unless @line1.strip() == DISPLAY_URI
  return false unless @line2
  return false unless m = @line2.match(/<http:\/\/vitro\.mannlib\.cornell\.edu\/ns\/vitro\/role#(.*)>/)
  @role = m[1]
end

def replicateProperty()
  newline1 = @line1.gsub(/Display/, "Publish")
  newline2 = @line2.gsub(/#.*>/, "##{@role}>")
  @lines.insert(@index + 2, newline1, newline2)
end

def checkForMismatch()
  return false unless @line1.strip() == DISPLAY_URI
  if !@line2
    puts "Found display property at end of file"
    return
  end
  if !@line2.match(/^\s*<http:\/\/vitro\.mannlib\.cornell\.edu\/ns\/vitro\/role#(.*)>\s*;\s*$/)
    puts "Found bogus clutter in the second line (#{@index}) '#{@line2}'"
    return
  end
end

def write_the_file()
  f = File.new(FILE_PATH+"-modified", "w") 
  @lines.each() do |line|
    f.write(line)
  end
  f.close()
end

read_the_file()
scan_the_lines()
write_the_file()

