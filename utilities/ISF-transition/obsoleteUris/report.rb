class Report
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  def relativize(path)
    Pathname.new(path).relative_path_from(Pathname.new(@directory_root)).to_s
  end

  def state_arguments()
  	puts
  	puts "-----------------------------------------------------------------"
  	puts " directory to scan = #{@args[0]}"
  	puts " obsolete_uris_file = #{@args[1]}"
  	puts " known exceptions file = #{@args[2]}"
  	puts " complete = #{!@args[3].nil?}"
  	puts "-----------------------------------------------------------------"
  	puts
  end
  
  def file_summary()
    puts " scanned #{@file_count} files"
    @extensions_count.sort.each do |pair|
      puts "    #{pair[0]}  #{pair[1]}"
    end
    puts "-----------------------------------------------------------------"
    puts
  end
 
  def collate_and_list_events()
    hash = Hash.new{|h, k| []}
    @events.each do |event|
      hash[event.path] = hash[event.path] << event
    end
    
    hash.sort.each do |path, events|
      puts "#{relativize(path)}"
      events.sort{|a, b| a.line_number <=> b.line_number }.each do |e|
        trimmed = 
          if e.line.size <= 100
            e.line 
          else
            e.line[0..97] << "..."
          end
        puts "   #{e.line_number} #{trimmed}"
        puts "       #{e.is_localname ? "Localname" : "URI"} #{e.string}"
      end
      puts "--------------------"
    end
  end 
    
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args, directory_root)
  	@args = args;
  	@directory_root = directory_root
    @file_count = 0
    @extensions_count = Hash.new(0)
    @events = []
  end
  
  def register_file(path)
    @file_count += 1
    @extensions_count[File.extname(path)] += 1
  end
  
  def add_event(event)
    @events << event
  end
  
  def report()
  	state_arguments()
  	file_summary()
  	collate_and_list_events()
  end
end
