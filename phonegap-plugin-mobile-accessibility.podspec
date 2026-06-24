Pod::Spec.new do |s|
  s.name             = 'phonegap-plugin-mobile-accessibility'
  s.version          = '2.0.0'
  s.summary          = 'PhoneGap Mobile Accessibility Plugin'
  s.license          = 'Apache 2.0'
  s.homepage         = 'https://github.com/phonegap/phonegap-mobile-accessibility'
  s.author           = 'Adobe PhoneGap Team'
  s.source           = { :git => 'https://github.com/phonegap/phonegap-mobile-accessibility.git', :tag => s.version.to_s }
  s.platform         = :ios, '11.0'
  s.source_files     = 'src/ios/*.{h,m}'
  s.weak_framework   = 'MediaAccessibility'
  s.dependency       'Cordova'
end
