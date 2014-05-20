class Criterion
  attr_accessor :expression
  attr_accessor :count
  attr_accessor :total_time
  def initialize(expr)
    @expression = Regexp.new(expr, Regexp::MULTILINE)
    @count = 0
    @total_time = 0.0
  end

  def add(log_record)
    @count += 1
    @total_time += log_record.time
  end

  def to_s()
    "#{self.class}: count=#{@count}, total_time=#{format("%.3f", @total_time)}, expression='#{@expression}'"
  end
end

class QueryCriterion < Criterion
  def test(log_record)
    if expression.match(log_record.query)
      add(log_record)
      return true
    end
  end
end

class StackCriterion < Criterion
  def test(log_record)
    if expression.match(log_record.stack.join())
      add(log_record)
      return true
    end
  end
end

class WritingCriterion < Criterion
  def initialize(expr, filename)
    super(expr)
    @file = File.new(filename, "w")
  end

  def add(log_record)
    super
    @file.write("#{@expression}\n\n--------------------------\n\n")
    @file.write(log_record.to_s)
    @file.write("\n--------------------------\n\n")
  end
end

class UnmatchedCriterion < WritingCriterion
  def initialize(filename)
    super("UNMATCHED", filename)
  end
end

class OverlapCriterion < WritingCriterion
  def initialize(filename)
    super("OVERLAP", filename)
  end
end

class Partition
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  def parse_partition_file(partition_file)
    @criteria = []
    File.open(partition_file).each() do |line|
      next if line.strip().empty? || line.start_with?('#')
      if line.start_with?("QUERY ")
        @criteria << QueryCriterion.new(line.slice(6..-1).strip)
      elsif line.start_with?("STACK ")
        @criteria << StackCriterion.new(line.slice(6..-1).strip)
      else
        raise "Invalid line in partition file: '#{line}'"
      end
    end
  end

  def match_against_criteria(log_record)
    matches = 0
    @criteria.each do |c|
      matches += c.test(log_record) ? 1 : 0
    end
    if matches == 0
      @unmatched.add(log_record)
    elsif matches > 1
      @overlap.add(log_record)
    end
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(partition_file, overlap_file, unmatched_file)
    parse_partition_file(partition_file)
    @overlap = OverlapCriterion.new(overlap_file)
    @unmatched = UnmatchedCriterion.new(unmatched_file)
  end

  def accumulate(log_record)
    match_against_criteria(log_record)
  end

  def report
    puts "#{@criteria.join("\n")}\n#{@overlap}\n#{@unmatched}"
  end

end
