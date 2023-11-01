
<p align="center">
  <a href="https://linkedin.com/in/sohrabonline">
    <img src="https://raw.githubusercontent.com/sohrabonline/scanned/master/assets/logo.png" width="640">
  </a>
  <h1 align="center">SCANNED</h1>
  <pre style="text-align: start;color: rgb(0, 0, 0);"><span style="color: rgb(0, 0, 0); font-family: Verdana, Geneva, sans-serif;">That's all, you've</span><span style="font-family: Verdana, Geneva, sans-serif;"> <strong><span style="color: rgb(44, 130, 201);">scanned</span></strong>!</span></pre>


Flutter plugin for scan barcode an QR in iOS and Android.

## Supported Platforms

| Platform | Check Connectivity | Listen for Changes |
| :------: |:------------------:| :----------------: |
| Android  |         ✅          |         ✅         |
|   iOS    |         ✅          |         ✅         |
|  macOS   |         ❌          |         ❌         |
|  Linux   |         ❌          |         ❌         |
| Windows  |         ❌          |         ❌         |
|   Web    |         ❌          |         ❌         |

### prepare

##### ios
info.list
```
<key>NSCameraUsageDescription</key>
<string>Your Description</string>

<key>io.flutter.embedded_views_preview</key>
<string>YES</string>
```
##### android
```xml
<uses-permission android:name="android.permission.CAMERA" />

<application>
  <meta-data
    android:name="flutterEmbedding"
    android:value="2" />
</application>
```

```yaml
dependencies:
  scanned: [latest version]
```
```dart
import 'package:scanned/scanned.dart';
```

```dart
  ScanController scanController = ScanController();

  Scanner(
      controller: scanController,
      scanAreaScale: .65,
      scanLineColor: Colors.blue,
      onCapture: (data) async {
              await  stopCamera();
              if (data.isNotNullOrEmpty) await onScanned(data);
              await Future.delayed(Duration(
              milliseconds: ((reactivateTime ??
              Configs.defaultCameraScanIdleTimeout) *
              1000)
                  .toInt()));
              await startCamera();
        }
    )
```


```dart
  ScanController scanController = ScanController();

  // toggle torch and get its status as bool
  final bool turnedOn = scanController.toggleTorchMode();

  //turn on Flash
  await scanController.turnOn();

  //turn off Flash
  await scanController.turnOff();
  
  // start or resume camera
  await scanController.resume();

  //pause camera
  await scanController.pause();

  //pause camera and turnOffFlash
  scanController.pause(turnOffFlash: true);
  
```