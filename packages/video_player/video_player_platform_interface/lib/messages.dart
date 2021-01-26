// Autogenerated from Pigeon (v0.1.7), do not edit directly.
// See also: https://pub.dev/packages/pigeon
// ignore_for_file: public_member_api_docs, non_constant_identifier_names, avoid_as, unused_import
// @dart = 2.8
import 'dart:async';
import 'dart:typed_data' show Uint8List, Int32List, Int64List, Float64List;

import 'package:flutter/services.dart';

class TextureMessage {
  int textureId;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['textureId'] = textureId;
    return pigeonMap;
  }

  // ignore: unused_element
  static TextureMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final TextureMessage result = TextureMessage();
    result.textureId = pigeonMap['textureId'];
    return result;
  }
}

class CreateMessage {
  String asset;
  String uri;
  String packageName;
  String formatHint;
  String title;
  String description;
  String previewUrl;
  bool pauseBackgroundVideo;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['asset'] = asset;
    pigeonMap['uri'] = uri;
    pigeonMap['packageName'] = packageName;
    pigeonMap['formatHint'] = formatHint;
    pigeonMap['title'] = title;
    pigeonMap['description'] = description;
    pigeonMap['previewUrl'] = previewUrl;
    pigeonMap['pause_background_video'] = pauseBackgroundVideo == true;
    return pigeonMap;
  }

  // ignore: unused_element
  static CreateMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final CreateMessage result = CreateMessage();
    result.asset = pigeonMap['asset'];
    result.uri = pigeonMap['uri'];
    result.packageName = pigeonMap['packageName'];
    result.formatHint = pigeonMap['formatHint'];
    result.description = pigeonMap['description'];
    result.title = pigeonMap['title'];
    result.previewUrl = pigeonMap['previewUrl'];
    result.pauseBackgroundVideo = pigeonMap['pause_background_video'] == true;
    return result;
  }
}

class LoopingMessage {
  int textureId;
  bool isLooping;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['textureId'] = textureId;
    pigeonMap['isLooping'] = isLooping;
    return pigeonMap;
  }

  // ignore: unused_element
  static LoopingMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final LoopingMessage result = LoopingMessage();
    result.textureId = pigeonMap['textureId'];
    result.isLooping = pigeonMap['isLooping'];
    return result;
  }
}

class VolumeMessage {
  int textureId;
  double volume;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['textureId'] = textureId;
    pigeonMap['volume'] = volume;
    return pigeonMap;
  }

  // ignore: unused_element
  static VolumeMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final VolumeMessage result = VolumeMessage();
    result.textureId = pigeonMap['textureId'];
    result.volume = pigeonMap['volume'];
    return result;
  }
}

class PlaybackSpeedMessage {
  int textureId;
  double speed;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['textureId'] = textureId;
    pigeonMap['speed'] = speed;
    return pigeonMap;
  }

  // ignore: unused_element
  static PlaybackSpeedMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final PlaybackSpeedMessage result = PlaybackSpeedMessage();
    result.textureId = pigeonMap['textureId'];
    result.speed = pigeonMap['speed'];
    return result;
  }
}

class PositionMessage {
  int textureId;
  int position;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['textureId'] = textureId;
    pigeonMap['position'] = position;
    return pigeonMap;
  }

  // ignore: unused_element
  static PositionMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final PositionMessage result = PositionMessage();
    result.textureId = pigeonMap['textureId'];
    result.position = pigeonMap['position'];
    return result;
  }
}

class MixWithOthersMessage {
  bool mixWithOthers;
  // ignore: unused_element
  Map<dynamic, dynamic> _toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['mixWithOthers'] = mixWithOthers;
    return pigeonMap;
  }

  // ignore: unused_element
  static MixWithOthersMessage _fromMap(Map<dynamic, dynamic> pigeonMap) {
    if (pigeonMap == null) {
      return null;
    }
    final MixWithOthersMessage result = MixWithOthersMessage();
    result.mixWithOthers = pigeonMap['mixWithOthers'];
    return result;
  }
}

class VideoPlayerApi {
  Future<void> initialize() async {
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.initialize', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(null);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<TextureMessage> create(CreateMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.create', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      return TextureMessage._fromMap(replyMap['result']);
    }
  }

  Future<void> dispose(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.dispose', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> setLooping(LoopingMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.setLooping', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> setVolume(VolumeMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.setVolume', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> setPlaybackSpeed(PlaybackSpeedMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.setPlaybackSpeed', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> play(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.play', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<PositionMessage> position(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.position', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      return PositionMessage._fromMap(replyMap['result']);
    }
  }

  Future<void> seekTo(PositionMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.seekTo', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> pause(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.pause', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> moveToPip(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.moveToPip', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<dynamic> openFullScreenMode(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.openFullScreenMode', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      return replyMap;
    }
  }

  Future<void> disableBackgroundMode(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.disableBackgroundMode', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> moveToBackgroundMode(TextureMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.moveToBackgroundMode', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }

  Future<void> setMixWithOthers(MixWithOthersMessage arg) async {
    final Map<dynamic, dynamic> requestMap = arg._toMap();
    const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
        'dev.flutter.pigeon.VideoPlayerApi.setMixWithOthers', StandardMessageCodec());

    final Map<dynamic, dynamic> replyMap = await channel.send(requestMap);
    if (replyMap == null) {
      throw PlatformException(
          code: 'channel-error',
          message: 'Unable to establish connection on channel.',
          details: null);
    } else if (replyMap['error'] != null) {
      final Map<dynamic, dynamic> error = replyMap['error'];
      throw PlatformException(
          code: error['code'], message: error['message'], details: error['details']);
    } else {
      // noop
    }
  }
}

abstract class TestHostVideoPlayerApi {
  void initialize();
  TextureMessage create(CreateMessage arg);
  void dispose(TextureMessage arg);
  void setLooping(LoopingMessage arg);
  void setVolume(VolumeMessage arg);
  void setPlaybackSpeed(PlaybackSpeedMessage arg);
  void play(TextureMessage arg);
  PositionMessage position(TextureMessage arg);
  void seekTo(PositionMessage arg);
  void pause(TextureMessage arg);
  void setMixWithOthers(MixWithOthersMessage arg);
  static void setup(TestHostVideoPlayerApi api) {
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.initialize', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        api.initialize();
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.create', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final CreateMessage input = CreateMessage._fromMap(mapMessage);
        final TextureMessage output = api.create(input);
        return <dynamic, dynamic>{'result': output._toMap()};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.dispose', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final TextureMessage input = TextureMessage._fromMap(mapMessage);
        api.dispose(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.setLooping', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final LoopingMessage input = LoopingMessage._fromMap(mapMessage);
        api.setLooping(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.setVolume', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final VolumeMessage input = VolumeMessage._fromMap(mapMessage);
        api.setVolume(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.setPlaybackSpeed', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final PlaybackSpeedMessage input = PlaybackSpeedMessage._fromMap(mapMessage);
        api.setPlaybackSpeed(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.play', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final TextureMessage input = TextureMessage._fromMap(mapMessage);
        api.play(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.position', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final TextureMessage input = TextureMessage._fromMap(mapMessage);
        final PositionMessage output = api.position(input);
        return <dynamic, dynamic>{'result': output._toMap()};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.seekTo', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final PositionMessage input = PositionMessage._fromMap(mapMessage);
        api.seekTo(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.pause', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final TextureMessage input = TextureMessage._fromMap(mapMessage);
        api.pause(input);
        return <dynamic, dynamic>{};
      });
    }
    {
      const BasicMessageChannel<dynamic> channel = BasicMessageChannel<dynamic>(
          'dev.flutter.pigeon.VideoPlayerApi.setMixWithOthers', StandardMessageCodec());
      channel.setMockMessageHandler((dynamic message) async {
        final Map<dynamic, dynamic> mapMessage = message as Map<dynamic, dynamic>;
        final MixWithOthersMessage input = MixWithOthersMessage._fromMap(mapMessage);
        api.setMixWithOthers(input);
        return <dynamic, dynamic>{};
      });
    }
  }
}
