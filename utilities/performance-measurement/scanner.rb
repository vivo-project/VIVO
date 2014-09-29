class LogRecord
  attr_accessor :time
  attr_accessor :method
  attr_accessor :query
  attr_accessor :stack
  def to_s
    "LogRecord: time=#{@time}\n   query: #{@query}\n   stack - #{@stack.size} lines:\n    #{@stack.join("    ")}"
  end
end

class LogFileParser
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  def get_line()
    if @line_buffer
      line = @line_buffer
      @line_buffer = nil
      return line
    elsif @file.eof?
      return nil
    else
      @lines += 1
      return @file.readline
    end
  end

  def unget_line(line)
    @line_buffer = line
  end

  def parse_first_line(line)
    match = /\[RDFServiceLogger\]\s+([0-9.]+).*\[(\w*, )?(.+)\]\s*$/.match(line)
    return nil unless match

    log_record = LogRecord.new
    log_record.time = match[1].to_f
    log_record.query = match[3].strip.gsub(/\s+/, ' ')
    log_record.stack = []
    return log_record
  end

  def is_stack_line(line)
    return false unless line
    return line.start_with?(' ')
  end

  def next_record()
    while true
      first_line = get_line
      return nil unless first_line
      log_record = parse_first_line(first_line)
      break if log_record
    end

    while true
      stack_line = get_line
      if is_stack_line(stack_line)
        log_record.stack << stack_line
      else
        unget_line(stack_line)
        return log_record
      end
    end
  end

  # ------------------------------------------------------------------------------------
  public

  # ------------------------------------------------------------------------------------
  #
  def parse_records()
    @file = File.new(@log_file)
    begin
      while true
        record = next_record()
        break unless record
        yield record
      end
    ensure
      @file.close
    end
  end

  def initialize(log_file)
    @log_file = log_file
    @line_buffer = nil
    @lines = 0
  end
  
  def report()
    puts "LogFileParser: read #{@lines} lines from '#{@log_file}'"
  end
end

class Scanner
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------
  def initialize(partition)
    @partition = partition
  end

  def process(log_file)
    lfp = LogFileParser.new(log_file)
    lfp.parse_records() do | log_record |
      @partition.accumulate(log_record)
    end
    lfp.report
  end
end
