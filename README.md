# <bindingName> Australian Bureau of Meteorology Weather Forecast Binding

This Eclipse Smarthome/openHAB binding allows retrieval of Australian weather forecast from Bureau of Meteorology.

## Observation and Forecast Features

This initial release maps most fields from BOM data-feed.

For today's observation and forecast these fields are available:

- Weather station
- Observation time
- Date and time of forecast
- Forecast icon name
- Precis (i.e. abstract)
- Forecast text
- Minimum temperature
- Maximum temperature
- Possibility of precipitation
- UV alert text
- Apparent temperature
- Air temperature
- Dew point
- Relative humidity
- Atmospheric pressure
- Wind direction
- Wind direction in degrees
- Wind speed in km/h
- Wind speed in Knots
- Rainfall
- 24 Hour rainfall

For future forecasts the following fields are available:

- Date and time of forecast
- Forecast icon name
- Precis (i.e. abstract)
- Forecast text
- Minimum temperature
- Maximum temperature
- Probability of precipitation
- Minimum precipitation
- Maximum precipitation
- UV alert text

## BOM Images

BOM images, like radar rain images, can retrieved and processed.  You have the option of:
- Retrieve the image series filenames (e.g radar loop images) for use in custom template with custom AngularJS animation code.
- Generate animated GIF of the images.
- Generate a series merged of PNG's of the images.

See below for more details.

## Prerequisite

- openHAB 2.4 and above.
- Java 1.8 and above.

## Installation

For openHAB install **Eclipse IoT Market** add-on under *MISC* tab in openHAB Paper UI.  Then install **Australian BOM Weather Forecast Binding** from the *Bindings* page.

For Eclipse SmartHome install from https://marketplace.eclipse.org/content/australian-bom-weather-forecast-binding.

## Configuration

At minimum there are five fields required to process the data-feed.  The observation product ID, the weather station ID of observation, the precis product ID, the city/town product ID and finally the area code.

Observation data-feed is required to show the current weather information.

Listed below is the observation product ID's for the states. Enter the ID you need into the *Observation product ID* field in Paper UI things configuration.

<table>
<tr align="left">
  <th>State</th>
  <th>Observation Product ID</th> 
</tr>
<tr>
  <td><b>NSW</b></td>
  <td>IDN60920</td>
</tr>
<tr>
  <td><b>ACT</b></td>
  <td>IDN60920</td>
</tr>
<tr>
  <td><b>NT</b></td>
  <td>IDD60920</td>
</tr>
<tr>
  <td><b>QLD</b></td>
  <td>IDQ60920</td>
</tr>
<tr>
  <td><b>SA</b></td>
  <td>IDS60920</td>
</tr>
<tr>
  <td><b>TAS</b></td>
  <td>IDT60920</td>
</tr>
<tr>
  <td><b>VIC</b></td>
  <td>IDV60920</td>
</tr>
<tr>
  <td><b>WA</b></td>
  <td>IDW60920</td>
</tr>
</table>

The next step is to open the product XML by loading `ftp://ftp.bom.gov.au/anon/gen/fwo/{product-id}.xml` in your browser and locate the weather station of interest.  Copy the `wmo-id` number and use this as the *Weather station ID*.

For example: *PERTH METRO* station ID in the file `ftp://ftp.bom.gov.au/anon/gen/fwo/IDW60920.xml` is `94608`.

Next you will need to provide the precis forecast product ID and city/town/district forecast product ID.

Precis forecast data provides brief forecast information for the next 5-8 days.  City/town/district forecast data provides the forecast description.

Below is a list of the forecast product ID's for Australian major cities.

<table>
<tr align="left">
  <th>City</th>
  <th>Precis Forecast Product ID</th> 
  <th>City Forecast Product ID</th> 
</tr>
<tr>
  <td><b>Darwin (NT)</b></td>
  <td>IDD10207</td>
  <td>IDD10150</td>
</tr>
<tr>
  <td><b>Canberra (ACT)</b></td>
  <td>IDN11060</td>
  <td>IDN10035</td>
</tr>
<tr>
  <td><b>Sydney (NSW)</b></td>
  <td>IDN11060</td>
  <td>IDN10064</td>
</tr>
<tr>
  <td><b>Newcastle (NSW)</b></td>
  <td>IDN11060</td>
  <td>IDN11051</td>
</tr>
<tr>
  <td><b>Central Coast (NSW)</b></td>
  <td>IDN11060</td>
  <td>IDN11052</td>
</tr>
<tr>
  <td><b>Wollongong (NSW)</b></td>
  <td>IDN11060</td>
  <td>IDN11053</td>
</tr>
<tr>
  <td><b>Alpine Centres (NSW)</b></td>
  <td>IDN11055</td>
  <td>IDN11055</td>
</tr>
<tr>
  <td><b>Brisbane (QLD)</b></td>
  <td>IDQ11295</td>
  <td>IDQ10095</td>
</tr>
<tr>
  <td><b>Gold Coast (QLD)</b></td>
  <td>IDQ11295</td>
  <td>IDQ10610</td>
</tr>
<tr>
  <td><b>Sunshine Coast (QLD)</b></td>
  <td>IDQ11295</td>
  <td>IDQ10611</td>
</tr>
<tr>
  <td><b>Adelaide (SA)</b></td>
  <td>IDS10044</td>
  <td>IDS10034</td>
</tr>
<tr>
  <td><b>Hobart (TAS)</b></td>
  <td>IDT16710</td>
  <td>IDT13600</td>
</tr>
<tr>
  <td><b>Launceston (TAS)</b></td>
  <td>IDT16710</td>
  <td>IDT13610</td>
</tr>
<tr>
  <td><b>Melbourne (VIC)</b></td>
  <td>IDV10753</td>
  <td>IDV10450</td>
</tr>
<tr>
  <td><b>Geelong (VIC)</b></td>
  <td>IDV10753</td>
  <td>IDV10701</td>
</tr>
<tr>
  <td><b>Mornington Peninsula (VIC)</b></td>
  <td>IDV10753</td>
  <td>IDV10702</td>
</tr>
<tr>
  <td><b>Perth (WA)</b></td>
  <td>IDW14199</td>
  <td>IDW12300</td>
</tr>
</table>

NOTE: If the forecast product ID's you are after are not in the list go to this catalogue page http://reg.bom.gov.au/catalogue/anon-ftp.shtml and search for the products.  The type must be "Forecast". Use the Search box on the page by entering, e.g "(WA)", or something more specific like "City Forecast":

1. Locate `Precis Forecast XML Package ({your-state})` from the search results and enter the product ID into the field *Precis forecast product ID* back in Paper UI Thing configuration.
2. Locate `City Forecast - {your-city} ({your-state})` for city forecasts OR `Town Forecast - {your-town} ({your-state})` for town forecasts OR `District Forecast - {your-district} ({your-state})` for district forecasts from the search results.  Enter the product ID into the configuration field *City/town/district forecast product ID* in Paper UI.

Now open either the precis or the city/town/district forecast XML (`ftp://ftp.bom.gov.au/anon/gen/fwo/{product-id}.xml`) and locate the area code (`aac` code).

For example: Perth's aac code in `ftp://ftp.bom.gov.au/anon/gen/fwo/IDW12300.xml` is `WA_PT053`.

For more information about data-feeds, please go to http://reg.bom.gov.au/catalogue/data-feeds.shtml

Screenshot below shows an example configuration in Paper UI.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration.png?raw=true" />


## Forecast Icons

The following table shows all the possible icon names returned by the channel.

<table>
<tr align="left">
  <th>Forecast</th>
  <th>Icon name</th>
</tr>
<tr>
  <td>Sunny</td>
  <td>sunny</td>
</tr>
<tr>
  <td>Clear</td>
  <td>clear</td>
</tr>
<tr>
  <td>Mostly sunny</td>
  <td>mostly-sunny</td>
</tr>
<tr>
  <td>Cloudy</td>
  <td>cloudy</td>
</tr>
<tr>
  <td>Hazy</td>
  <td>hazy</td>
</tr>
<tr>
  <td>Light rain</td>
  <td>light-rain</td>
</tr>
<tr>
  <td>Windy</td>
  <td>windy</td>
</tr>
<tr>
  <td>Fog</td>
  <td>fog</td>
</tr>
<tr>
  <td>Shower</td>
  <td>shower</td>
</tr>
<tr>
  <td>Rain</td>
  <td>rain</td>
</tr>
<tr>
  <td>Dusty</td>
  <td>dusty</td>
</tr>
<tr>
  <td>Frost</td>
  <td>frost</td>
</tr>
<tr>
  <td>Snow</td>
  <td>snow</td>
</tr>
<tr>
  <td>Storm</td>
  <td>storm</td>
</tr>
<tr>
  <td>Light shower</td>
  <td>light-shower</td>
</tr>
<tr>
  <td>Heavy shower</td>
  <td>heavy-shower</td>
</tr>
<tr>
  <td>Cyclone</td>
  <td>cyclone</td>
</tr>
</table>  

## Items mapping file

Creating items and linking them for eight days of forecasts can be tedious.  Provided below is the items mapping file that you can drop into the "items" folder, typically in `/etc/openhab2/items` under Linux or `C:\openHAB2\conf\items` under Windows.  The prerequisite is to name the BOM Thing ID "default".  If you would like name your BOM Thing ID as something else, edit the file and rename accordingly.

https://github.com/tomitan100/org.openhab.binding.bom/raw/master/doc/bom.items

## BOM Images

__Background__
BOM images, like rain radar loop, are made up of a series of transparent PNG files, which get updated frequently as data is available.  These images contain only the radar scans and do not include the background image, topography, locations, etc of Australia.  These images are known as transparencies.  To get the final image each of the radar images must be merged with the transparencies in the correct order.

BOM Image binding can create the final images of each series as PNG's and/or animated GIF.  This makes it easier to display radar loops in the web browser without having to code Javascript to loop through the images.

Radar images are stored in ftp://ftp.bom.gov.au/anon/gen/radar/ while transparancies are stored in ftp://ftp.bom.gov.au/anon/gen/radar_transparencies/

## BOM Images Configuration

The first step is to figure out the product ID of the images you are after.  You can do this easily by searching "IDR" in BOM's catalogue page http://reg.bom.gov.au/catalogue/anon-ftp.shtml.

Note that each radar range is under different product ID.

Examples for Perth radar loop:
- IDR701 - 512 km
- IDR702 - 256 km
- IDR703 - 128 km
- IDR704 - 64 km

In the configuration screen as shown below, typically you would only care about changing the Product ID to the one you would like to show, and turning on Generated animated GIF.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration-image-sources.png?raw=true" />
<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration-image-generation.png?raw=true" />

On this screen you also have the option to modify the layer ordering, add additional layer, generate PNG images, change the delay between GIF images in the animated gif, enable GIF looping, apply post processing to the image, change image output path and output filename.

__Image Layers Configuration__

Each layer is separated by a semicolon and each setting for the layer is separated by a comma.  The order of the layer determines the layer merge order.

Each layer at minimum have the image name.  The layer where each of the series images to be assigned must be named `${series}`.

For example (taken from default configuration):

`image=${pid}.background.png; image=${pid}.topography.png; image=${series}; image=${pid}.locations.png; image=${pid}.range.png`

Explanation:
- There are five layers that make up the final image: background, topography, ${series} image, locations and range.
- Layer 1 will be obscured by layer 2, layer 2 will be obscured by layer 3, and so on.
- Layer 3, `image=${series}` is the placholder for the image series.
- `${pid}` is the placeholder for product ID.  If your product ID is IDR701 then it is equivalent to use image=IDR701.background.png as the first layer.
- These images are sourced from ftp://ftp.bom.gov.au/anon/gen/radar_transparencies/.  Other transparancies available are: `${pid}.wthrDistricts.png`, `${pid}.waterways.png`, `${pid}.roads.png`, `${pid}.rail.png`, `${pid}.catchments.png`.  To see what else are available go to the FTP directory.
- When using _Rainfall_ series, you must not use `${pid}` as there does not seem to be equivalently named transparencies.  You will have to hardcode the one of the radar product code in the layers configuration.
- It is possible to add image processing operation per layer.  See below for more details.

__Image Processing__

Currently there are three image operations available to each layer and to the final image:
- Opacity - changes the opacity of the layer/final image.
- Resize - resizes the layer/final image.
- Crop - crops the layer/final image.

Opacity accepts one argument, a value between 0 and 1, inclusive. e.g `opacity=0.5`

Resize requires two arguments: width and height in pixels. e.g `resize=600 600`

Crop requires four arguments:  x, y, width and height. e.g `0 12 512 500`

You can chain those operations in each layer or final image.

_Example usage in a layer:_

`image=${pid}.range.png, opacity=0.5`

_Example usage in image post-processing field:_

`crop=0 10 512 502, resize=600 600`

## Example Screenshots in openHAB HABPanel

The screenshots below are examples of the binding in operation.  The screens use custom theme called "Matrix Theme" by Patrick (`@pmpkk`).  For more information about the theme please go to https://community.openhab.org/t/matrix-theme-for-habpanel/31100.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/home.png?raw=true" />

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/forecast.png?raw=true" />

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/radar-loop.gif?raw=true" />

## Change log

__14/04/2019__
- Added retain min/max temperatures for today
- Added BOM radar/rainfall image generation support

__08/03/2019__
- Fixed icon mapping for light rain

__03/03/2019__
- Fixed apparent vs air temperature mix up
- Fixed today's min/max temperature

__26/02/2019__
- Added 24 hour rainfall channel
- Added minimum and maximum precipitation

__18/02/2019__
- Added weather station channel and additional logging

__17/02/2019__
- Updated channel label names

__16/02/2019__
- Added apparent temperature from observation

__14/02/2019__
- Added support for city/town and district forecast data
- Handled NaN values from data

__13/02/2019__
- Updated forecast date and time label

__12/02/2019__
- Initial release
