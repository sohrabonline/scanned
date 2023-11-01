import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'scanned_method_channel.dart';

abstract class ScannedPlatform extends PlatformInterface {
  /// Constructs a ScannedPlatform.
  ScannedPlatform() : super(token: _token);

  static final Object _token = Object();

  static ScannedPlatform _instance = MethodChannelScanned();

  /// The default instance of [ScannedPlatform] to use.
  ///
  /// Defaults to [MethodChannelScanned].
  static ScannedPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ScannedPlatform] when
  /// they register themselves.
  static set instance(ScannedPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
