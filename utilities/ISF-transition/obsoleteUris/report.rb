class Report
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

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
  end
  
  def list_events()
    @events.each do |event|
      puts "Event: #{event}"
    end
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
  	@args = args;
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
  	list_events()
  end
end
