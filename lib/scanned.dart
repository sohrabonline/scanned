import 'scanned_platform_interface.dart';

import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Scanned {
  static const MethodChannel _channel = const MethodChannel('scanned');
//
  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> parse(String path) async {
    final String? result = await _channel.invokeMethod('parse', path);
    return result;
  }
}

class Scanner extends StatefulWidget {
  Scanner({
    this.controller,
    this.onCapture,
    this.scanLineColor = Colors.green,
    this.scanAreaScale = 0.7,
  })  : assert(scanAreaScale <= 1.0, 'scanAreaScale must <= 1.0'),
        assert(scanAreaScale > 0.0, 'scanAreaScale must > 0.0');

  final ScanController? controller;
  final CaptureCallback? onCapture;
  final Color scanLineColor;
  final double scanAreaScale;

  @override
  State<StatefulWidget> createState() => _ScannerState();
}

class _ScannerState extends State<Scanner> {
  MethodChannel? _channel;

  @override
  Widget build(BuildContext context) {
    if (Platform.isIOS) {
      return UiKitView(
        viewType: 'scan_view',
        creationParamsCodec: StandardMessageCodec(),
        creationParams: {
          "r": widget.scanLineColor.red,
          "g": widget.scanLineColor.green,
          "b": widget.scanLineColor.blue,
          "a": widget.scanLineColor.opacity,
          "scale": widget.scanAreaScale,
        },
        onPlatformViewCreated: (id) {
          _onPlatformViewCreated(id);
        },
      );
    } else {
      return AndroidView(
        viewType: 'scan_view',
        creationParamsCodec: StandardMessageCodec(),
        creationParams: {
          "r": widget.scanLineColor.red,
          "g": widget.scanLineColor.green,
          "b": widget.scanLineColor.blue,
          "a": widget.scanLineColor.opacity,
          "scale": widget.scanAreaScale,
        },
        onPlatformViewCreated: (id) {
          _onPlatformViewCreated(id);
        },
      );
    }
  }

  void _onPlatformViewCreated(int id) {
    _channel = MethodChannel('scanned/method_$id');
    _channel?.setMethodCallHandler((MethodCall call) async {
      if (call.method == 'onCaptured') {
        if (widget.onCapture != null)
          widget.onCapture!(call.arguments.toString());
      }
    });
    widget.controller?._channel = _channel;
  }
}

typedef CaptureCallback(String data);

class ScanArea {
  const ScanArea(this.width, this.height);

  final double width;
  final double height;
}

class ScanController {
  MethodChannel? _channel;

  ScanController();

  Future<void> resume() async {
    await _channel?.invokeMethod("resume");
  }

  Future<void> pause({bool turnOffFlash = false}) async {
    if (turnOffFlash) await _channel?.invokeMethod("turnOff");
    await _channel?.invokeMethod("pause");
  }

  Future<bool> toggleTorchMode() async {
    return await _channel?.invokeMethod("toggleTorchMode");
  }

  Future<bool> turnOn() async {
    return await _channel?.invokeMethod("turnOn");
  }

  Future<bool> turnOff() async {
    return await _channel?.invokeMethod("turnOff");
  }
}
