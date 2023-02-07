package com.cartrade.cameralib.camerafiles.camera2.dimension;

import androidx.annotation.NonNull;


/**
 * Created by sudheer on 8/21/17.
 */

public final class AspectRatio implements Comparable<AspectRatio> {
    private int width;
    private  int height;
    private AspectRatio(int w, int h) {
        width = w;
        height = h;
    }
    public static AspectRatio of(int w, int h) {
        int gcd = gcd(w, h);
        return new AspectRatio(w/gcd, h/gcd);
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
        return a;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AspectRatio inverse() {
        //noinspection SuspiciousNameCombination
        return AspectRatio.of(height, width);
    }

    @Override
    public int compareTo(@NonNull AspectRatio o) {
        if (equals(o)) {
            return 0;
        }
        return toDouble() > o.toDouble() ? 1 : -1;
    }

    public double toDouble() {
        return  (double) width / height;
    }

    @Override
    public int hashCode() {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return height ^ ((width << (Integer.SIZE / 2)) | (width >>> (Integer.SIZE / 2)));
    }


    public static AspectRatio parse(String s) {
        int position = s.indexOf(':');
        if (position == -1) {
            throw new IllegalArgumentException("Malformed aspect ratio: " + s);
        }
        try {
            int x = Integer.parseInt(s.substring(0, position));
            int y = Integer.parseInt(s.substring(position + 1));
            return AspectRatio.of(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Malformed aspect ratio: " + s, e);
        }
    }


    public boolean matches(Size size) {
        int gcd = gcd(size.getWidth(), size.getHeight());
        int x = size.getWidth() / gcd;
        int y = size.getHeight() / gcd;
        return width == x && height == y;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof AspectRatio)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        AspectRatio a = (AspectRatio)o;
        return a.width == width && a.height == height;
    }

    @Override
    public String toString() {
        return width + "/" + height;
    }
}
