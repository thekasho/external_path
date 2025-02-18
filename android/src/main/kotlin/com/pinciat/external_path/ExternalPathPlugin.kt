package com.pinciat.external_path

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.os.Environment
import android.content.Context
import java.io.File
import android.os.Build
import kotlin.collections.ArrayList

/** ExternalPathPlugin */
class ExternalPathPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "external_path")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "getExternalStorageDirectories" -> result.success(getExternalStorageDirectories())
            "getExternalStoragePublicDirectory" -> result.success(getExternalStoragePublicDirectory(call.argument<String>("type")))
            else -> result.notImplemented()
        }
    }

    private fun getExternalStorageDirectories(): ArrayList<String> {
        val appsDir: Array<File> = context.getExternalFilesDirs(null)
        val extRootPaths = ArrayList<String>()
        for (file in appsDir) {
            extRootPaths.add(file.absolutePath)
        }
        return extRootPaths
    }

    private fun getExternalStoragePublicDirectory(type: String?): String {
        return Environment.getExternalStoragePublicDirectory(type).toString()
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
