/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.ColorRes;
import com.f2prateek.ln.Ln;
import java.io.IOException;

public class Utils {

  private Utils() {
    throw new AssertionError("no instances.");
  }

  // Color

  public static int getColor(Context context, @ColorRes int resourceId, int defaultColor) {
    // Workaround for http://crashes.to/s/88afcd93aba
    try {
      return context.getResources().getColor(resourceId);
    } catch (Resources.NotFoundException e) {
      Ln.e(e);
      return defaultColor;
    }
  }

  // Strings

  public static boolean isBlank(CharSequence string) {
    return (string == null || string.toString().trim().length() == 0);
  }

  public static String valueOrDefault(String string, String defaultString) {
    return isBlank(string) ? defaultString : string;
  }

  public static String truncateAt(String string, int length) {
    return string.length() > length ? string.substring(0, length) : string;
  }

  // Storage

  /**
   * Checks if storage is available for our use.
   *
   * @return true if storage is available and writeable
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted") public static boolean isStorageAvailable() {
    boolean externalStorageAvailable;
    boolean externalStorageWriteable;
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      // We can read and write the media
      externalStorageAvailable = externalStorageWriteable = true;
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      // We can only read the media
      externalStorageAvailable = true;
      externalStorageWriteable = false;
    } else {
      // Something else is wrong. It may be one of many other states, but
      // all we need to know is we can neither read nor write
      externalStorageAvailable = externalStorageWriteable = false;
    }

    Ln.i(externalStorageAvailable ? "Storage available" : "Storage Unavailable");
    Ln.i(externalStorageWriteable ? "Storage writeable" : "Storage not writeable");

    return (externalStorageAvailable && externalStorageWriteable);
  }

  // Bitmap

  /**
   * Returns a mutable bitmap from a uri.
   *
   * @param uri Uri to the file
   * @return A mutable copy of the decoded {@link android.graphics.Bitmap}; null if failed.
   * @throws java.io.IOException if unable to make it mutable
   */
  public static Bitmap decodeUri(final ContentResolver resolver, final Uri uri) throws IOException {
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inJustDecodeBounds = false;
    opt.inMutable = true;
    return BitmapFactory.decodeStream(resolver.openInputStream(uri), null, opt);
  }

  /**
   * Returns a mutable bitmap from a resource.
   *
   * @param context Everything needs a context =(
   * @return A mutable copy of the resource
   */
  public static Bitmap decodeResource(final Context context, final String resourceName) {
    Resources resources = context.getResources();
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inMutable = true;
    return BitmapFactory.decodeResource(resources,
        getResourceIdentifierForDrawable(context, resourceName), opt);
  }

  /**
   * Get the identifier for a drawable resource with the given name
   *
   * @param context Everything needs a context =(
   * @param resourceName Name of the resource
   */
  public static int getResourceIdentifierForDrawable(final Context context,
      final String resourceName) {
    Resources resources = context.getResources();
    String packageName = context.getPackageName();
    return resources.getIdentifier(resourceName, "drawable", packageName);
  }
}
