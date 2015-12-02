class ObsoleteUrisError < StandardError; end

class ObsoleteUris
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  def get_localname(uri)
    delimiter = uri.rindex(/[\/#]/)
    return uri[delimiter+1..-1] if delimiter
    raise "BOGUS URI in obsolete_uris file -- no localname: '#{uri}'"
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(file)
    @uris = []
    @localnames = []
    File.read(file).split(/[\r\n]+/).each do |line|
      # ignore blank lines, and lines starting with '#' or '!'.
      line.strip!
      next if line.length == 0 || line[0..0] == '#' || line[0] == ?!

      if line =~ /^(\S+)/
        @uris << $1
        @localnames << get_localname($1)
      else
        raise "BOGUS line in obsolete_uris file: '#{line}'"
      end
    end
  end
  
  def uris()
    @uris
  end

  def localnames()
    @localnames
  end
end
