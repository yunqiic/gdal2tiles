#!/usr/bin/env python
# -*- coding: utf-8 -*-

from numpy import *
# import math
import numpy

if __name__ == '__main__':
    src = open("D:\\java\\83083.terrain", 'rb+')
    data = src.read(2)
    while data:
        a = fromstring(data, dtype=numpy.int16)
        if a >= -1000:
            a = (a + 1000) * 5
        else:
            # set NODATA TO NULL
            a = a - a
            a = (a + 1000) * 5
        data=src.read(2)
    print ""
