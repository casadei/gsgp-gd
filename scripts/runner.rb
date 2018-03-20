#!/usr/bin/env ruby

# PARAMETERS
DATASETS     = %w(airfoil ccn ccun concrete energyCooling energyHeating keijzer-5 keijzer-6 keijzer-7 keijzer-8 parkinsons ppb towerData vladislavleva-1 wineRed wineWhite yacht)
STRATEGIES   = %w(sigmoid percentileminmax-90 percentileminmax-80)
BUILDERS     = %w(GROW RHH FULL)

# PATHS
MASTER_PATH  = "./experiments/scripts/masterGSGP.param"
PARAMS_PATH  = "./experiments/scripts/gsgp"
BIN_PATH     = "./dist/GSGP.jar"

def update_parameters!(builder, normalization)
  lines = File.readlines(MASTER_PATH)
  lines[-3] = "tree.build.builder.random.tree = #{builder}\n"
  lines[-1] = "normalization.strategy = #{normalization}"

  File.open(MASTER_PATH, 'w') { |f| f.write(lines.join) }
end

def print_and_flush(message)
  $stdout.print "#{message}\n"
  $stdout.flush
end

def execute(dataset, normalization, builder)
  print_and_flush "*** Remove existing data."

  %x(rm -rf /tmp/*-sgp)

  print_and_flush "*** SET tree.build.builder.random.tree = #{builder} "
  print_and_flush "*** SET normalization.strategy = #{normalization}"
  update_parameters!(builder, normalization)

  print_and_flush "*** Running experiments for dataset: #{dataset}"

  IO.popen("java -jar -Xmx12g #{BIN_PATH} -p #{PARAMS_PATH}/#{dataset}.param 2>&1 > /tmp/output.log") do |pipe|
    pipe.each do |line|
      print_and_flush line
    end
  end

  print_and_flush "*** Done!"
  print_and_flush "*** Move results to ~/results/#{normalization}/#{builder} folder"

  %x{mkdir -p ~/results}
  %x{mkdir -p ~/results/norm-#{normalization}}
  %x{mkdir -p ~/results/norm-#{normalization}/#{builder}}
  %x(mv /tmp/*-sgp ~/results/norm-#{normalization}/#{builder})

end

DATASETS.product(STRATEGIES, BUILDERS).each do |dataset, strategy, builder|
  execute(dataset, strategy, builder)
end
