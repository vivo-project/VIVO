class Criterion
  attr_accessor :expression
  attr_accessor :group_name
  attr_accessor :count
  attr_accessor :total_time
  def initialize(expr, current_group_name='')
    expr = expr.gsub(/\s+/, ' ')
    @expression = Regexp.new(expr, Regexp::MULTILINE)
    @group_name = current_group_name || ''
    @count = 0
    @total_time = 0.0
  end

  def add(log_record)
    @count += 1
    @total_time += log_record.time
  end

  def to_s()
    format("%s: count=%5d, total_time=%9.3f, expression='%s'", self.class, @count, @total_time, @expression.source)
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

class GroupList
  def initialize(criteria)
    @list = []
    criteria.each do |c|
      add_to_group(c)
    end
  end

  def add_to_group(c)
    @list.each do |g|
      if g.name == c.group_name
        g.add(c)
        return
      end
    end
    @list << Group.new(c)
  end

  def to_s()
    total_count = @list.inject(0) {|sum, g| sum + g.total_count}
    total_time = @list.inject(0.0) {|sum, g| sum + g.total_time}
    format("%s \nALL GROUPS: total count = %5d, total time = %9.3f seconds \n", @list.join("\n"), total_count, total_time)
  end

  class Group
    attr :name
    attr :criteria
    attr :total_count
    attr :total_time
    def initialize(c)
      @name = c.group_name
      @criteria = []
      @total_count = 0
      @total_time = 0.0
      add(c)
    end

    def add(c)
      @criteria << c
      @total_count += c.count
      @total_time += c.total_time
    end

    def to_s()
      format("GROUP '%s' \n  %s\n %20s total count = %5d, total time = %9.3f seconds \n", @name, @criteria.join("\n  "), ' ', @total_count, @total_time)
    end
  end
end

class Partition
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  def parse_partition_file(partition_file)
    @criteria = []
    current_group_name = ''
    File.open(partition_file).each() do |line|
      next if line.strip().empty? || line.start_with?('#')
      if line.start_with?("QUERY ")
        @criteria << QueryCriterion.new(line.slice(6..-1).strip, current_group_name)
      elsif line.start_with?("STACK ")
        @criteria << StackCriterion.new(line.slice(6..-1).strip, current_group_name)
      elsif line.start_with?("GROUP ")
        current_group_name = line.slice(6..-1).strip
      elsif line.start_with?("GROUP")
        current_group_name = ''
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
    puts "#{GroupList.new(@criteria)}\n#{@overlap}\n#{@unmatched}\n"
  end

end
