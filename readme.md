# Outline Calculator

This app allows you to measure areas on a picture, either taken with the camera
or by selecting from storage, based on outlines of buildings, areas, etc.

The area is calculated based on a legend on the image, which is the scale factor to calculate
between "pixels" and real measure values.

## Handling
1. You first select an image, either by taken one with the camera or by selecting 
an existing file from local storage.
2. Set the legend "value": This is the value mentioned inside the image and defines
the size of the legend in "reality" (e.g. 10m, 100m). Value should only be the
numerical value, not the unit.
3. Move the overlay so left and right borders match exactly the start and end
of the legend. (The height is not important here, so can be whatever you mark it)
4. Click the "legend size" button: this will get the width of the marked legend
and calculates based on the given legend value the factor to convert pixels
into real values.
5. Now move the overlay and resize it so it captures the area which you want to
measure.
6. Click "Area": this will calculate the area in "real" values, meaning if the
legend is given in meters, the area value represents square meters.

## Functions
- Load image from camera intent or from local media storage
- rotate (90 degress clockwise, step-by-step)
- clip to remove unnecessary content of image

## Used Libraries
- com.isseiaoki.simplecropview which defines the "crop" overlay

## Improvements
- zooming not working yet
- ...
Still a lot of work to do... ;-)

## License
