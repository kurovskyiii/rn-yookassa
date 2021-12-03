require "json"

# use_frameworks!
# source = 'https://github.com/yoomoney/yookassa-payments-swift.git'

# source 'https://github.com/CocoaPods/Specs.git'
# source 'https://github.com/yoomoney-tech/cocoa-pod-specs.git'

# plugin 'cocoapods-user-defined-build-types'
# enable_user_defined_build_types!

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "rn-yookassa"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "https://github.com/kurovskyi/rn-yookassa.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.requires_arc = true
  s.static_framework = true

  s.dependency "React-Core"
  s.dependency "YooKassaPayments"
  s.dependency "CardIO", "~> 5.4.1"

end
