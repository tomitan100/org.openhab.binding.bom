# <bindingName> Australian Bureau of Meteorology Weather Forecast and Image Binding

This binding retrieves Australian weather forecast and meteorological images from Bureau of Meteorology for use in openHaB/Eclipse Smarthome.

## Contents

<ul>
  <li><a href="#features">Features</a></li>
  <li type="none">
    <ul type="disc">
      <li><a href="#observation-and-forecast-features">Observation and forecast features</a></li>
      <li><a href="#bom-images-features">BOM images features</a></li>
    </ul>
  </li>
  <li><a href="#prerequisite">Prerequisite</a></li>
  <li><a href="#installation">Installation</a></li>
  <li><a href="#weather-observation-and-forecast-configuration">Weather observation and forecast configuration</a></li>
  <li><a href="#weather-forecast-icons">Weather forecast icons</a></li>
  <li><a href="#bom-weather-items-mapping-and-sitemap-files">BOM weather items mapping and sitemap files</a></li>
  <li><a href="#bom-images">BOM weather images generation</a></li>
  <li type="none">
    <ul type="disc">
      <li><a href="#background-information">BOM images background information</a></li>
      <li><a href="#bom-images-configuration">BOM images configuration</a></li>
      <li><a href="#image-sources-configuration-fields">Image sources configuration fields</a></li>
      <li><a href="#image-generation-configuration-fields">Image generation configuration fields</a></li>
      <li><a href="#image-layers-configuration">Image layers configuration</a></li>
      <li><a href="#image-manipulation-and-processing">Image manipulation and processing</a></li>
      <li><a href="#local-timestamp-configuration">Local timestamp configuration</a></li>
      <li><a href="#rain-radar-images-configuration-example">Rain radar images configuration example</a></li>
      <li><a href="#doppler-wind-images-configuration-example">Doppler wind images configuration example</a></li>
      <li><a href="#rainfall-images-configuration-example">Rainfall images configuration example</a></li>
      <li><a href="#satellite-images-configuration-example">Satellite images configuration example</a></li>
      <li type="none">
        <ul type="disc">
          <li><a href="#other-satellite-view-variations">Other satellite view variations</a></li>
        </ul>
      </li>
      <li><a href="#high-definition-himawari-8-satellite-images-configuration-example">High-definition Himawari-8 satellite images configuration example</a></li>
      <li><a href="#mean-sea-level-pressure-images-configuration-example">Mean sea-level pressure images configuration example</a></li> 
      <li><a href="#using-the-images">Using the images</a></li>
      <li><a href="#unsupported-charts">Unsupported charts</a></li>
    </ul>
  </li>
  <li><a href="#example-screenshots-in-openhab-habpanel">Example screenshots in openHAB HABPanel</a></li>
  <li><a href="#change-log">Change log</a></li>
</ul>

## Features

### Observation and Forecast Features

This binding maps most fields from BOM data-feed.  For today's observation and forecast these are available:

- Weather station
- Observation time
- Date and time of forecast
- Forecast icon name
- Precis
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
- Precis
- Forecast text
- Minimum temperature
- Maximum temperature
- Probability of precipitation
- Minimum precipitation
- Maximum precipitation
- UV alert text

### BOM Images Features

BOM images, like rain radar, rainfall and satellite images, can retrieved and processed.  You have the option of:
- Retrieve the image sequence filenames for use in custom template with custom AngularJS animation code.
- Generate animated GIF from a sequence images.
- Generate individual PNG images from a sequence of images.

See below for more details.

## Prerequisite

#### For openHAB 2
- openHAB 2.4 to 2.5.#
- Java 1.8 and above.
- Fonts installed if local timestamp is enabled for BOM Image.

#### For openHAB 3
- openHAB 3.0.0 and above
- Java 11 and above.
- Fonts installed if local timestamp is enabled for BOM Image.

## Installation

### Via Eclipse IoT Market - for openHAB 2 only
For openHAB install **Eclipse IoT Market** add-on under *MISC* tab in openHAB Paper UI.  Then install **Australian BOM Weather Forecast Binding** from the *Bindings* page.

For Eclipse SmartHome install from https://marketplace.eclipse.org/content/australian-bom-weather-forecast-binding.

### Manual installation - for openHAB 2 and openHAB 3
Download the latest jar below for your openHAB version and copy to the openHAB `addons` directory.

#### openHAB 3
Version 3.0.0 [Download](https://github.com/tomitan100/org.openhab.binding.bom/raw/master/dist/org.openhab.binding.bom-3.0.0-SNAPSHOT.jar)

#### openHAB 2
Version 2.5.9 [Download](https://github.com/tomitan100/org.openhab.binding.bom/raw/master/dist/org.openhab.binding.bom-2.5.9-SNAPSHOT.jar)

Version 2.5.0 [Download](https://github.com/tomitan100/org.openhab.binding.bom/raw/master/dist/org.openhab.binding.bom-2.5.0-SNAPSHOT.jar)

## Weather observation and forecast configuration

Unfortunately, it can be quite daunting to configure this binding due to the lack of index of all the data files available from BOM.  Hopefully the details below is sufficient to get the binding up and running on your openHAB.  In the future an external tool might be coded to make this binding less difficult to configure.

At minimum there are five values required to process the data-feed from BOM.  The observation product ID, the weather station ID of observation, the precis product ID, the city/town product ID and finally the area code.

Observation product ID and weather station enables you show the relevant current weather information in your area.  Precis forecast data provides brief forecast information for the next 5-8 days.  City/town/district forecast data provides the detailed forecast description.

Listed below is the observation product ID's for the states. Enter the ID you need into the *Observation product ID* field in Things configuration in openHAB.

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

The next step is to open the product XML by loading `ftp://ftp.bom.gov.au/anon/gen/fwo/{product-id}.xml` in your browser.  Replace `{product-id}` with the ID of your state from the table above.  Locate the weather station of interest.  Copy the `wmo-id` number and use this as the *Weather station ID*.

For example: *PERTH METRO* station ID in the file `ftp://ftp.bom.gov.au/anon/gen/fwo/IDW60920.xml` is `94608`.

Next you will need to provide the precis forecast product ID and city/town/district forecast product ID.

Below is a list of the forecast product ID's for Australian _major_ cities.  If you do not live in the cities listed below then follow the instruction to locate your city, town or district.

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

1. Locate `Precis Forecast XML Package ({your-state})` from the search results and enter the product ID into the field *Precis forecast product ID* back in Thing configuration.
2. Locate `City Forecast - {your-city} ({your-state})` for city forecasts OR `Town Forecast - {your-town} ({your-state})` for town forecasts OR `District Forecast - {your-district} ({your-state})` for district forecasts from the search results.  Enter the product ID into the configuration field *City/town/district forecast product ID* in openHAB configuration.

Now open either the precis or the city/town/district forecast XML (`ftp://ftp.bom.gov.au/anon/gen/fwo/{product-id}.xml`) and locate the area code (`aac` code).

For example: Perth's aac code in `ftp://ftp.bom.gov.au/anon/gen/fwo/IDW12300.xml` is `WA_PT053`.

Save your configuration.  openHAB will begin to download all the required weather information in a few seconds and made available to you to display.

For more information about data-feeds, please go to http://reg.bom.gov.au/catalogue/data-feeds.shtml

Screenshot below shows an example configuration in Paper UI.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration.png?raw=true" />


## Weather Forecast Icons

The following table shows all the possible icon names returned by the channel. You may use these to determine which forecast icon to show in the mobile app/browser.  Setting this up is beyond the scope of this document.  Discussion about this is available in the openHAB community forum.

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

## BOM Weather Items mapping and sitemap files

Creating items and linking them for eight days of forecasts can be tedious.  Provided below is the items mapping file that you can drop into the "items" folder, typically in `/etc/openhab2/items` under Linux or `C:\openHAB2\conf\items` under Windows.  The prerequisite is to name the BOM Thing ID "default".  If you would like name your BOM Thing ID as something else, edit the file and rename accordingly.

https://github.com/tomitan100/org.openhab.binding.bom/raw/master/doc/bom.items

Also provided below is the site map where you can modify and drop into `/etc/openhab2/sitemaps`.

https://github.com/tomitan100/org.openhab.binding.bom/raw/master/doc/bom.sitemap


## BOM Weather Images Generation

### Background Information
BOM images, like rain radar loop, are made up of a series of transparent PNG files, which get updated frequently as data is made available.  These images contain only the transparent radar/satellite scans and do not include static overlays like the the background, topography, locations, borders, etc.  The final image is built by overlaying all the images in the correct order.

BOM Image binding can create final image(s) of each radar or satellite image sequence or series as PNG images or a single animated GIF or both.  This makes it easier for you to display radar loops in the browser/viewer without having to code Javascript to assemble the image overlays.

By default, BOM Image binding retrieves rainfall radar sequence of images from ftp://ftp.bom.gov.au/anon/gen/radar/ and static overlay images from ftp://ftp.bom.gov.au/anon/gen/radar_transparencies/.

You have the option to use images from different paths to generate other kinds of weather images.  Please look at example configurations for other images [here](#rain-radar-images-configuration-example).

## BOM Images Configuration

Configuring weather images does look daunting at first but it is not.  If you read through the instruction below you should have no problems.

For rainfal radar images, the first step is to determine the product ID of the images you are after.  You can do this easily by searching "IDR" in BOM's catalogue page http://reg.bom.gov.au/catalogue/anon-ftp.shtml.  Another way is to note the product ID in the "Rainfall Radars" URL itself.  e.g http://www.bom.gov.au/products/IDR701.loop.shtml.

Note that each radar range is under different product ID.

Examples for Perth radar loop:
- IDR701 - 512 km
- IDR702 - 256 km
- IDR703 - 128 km
- IDR704 - 64 km

In the configuration screen typically you would only care about changing the Product ID to the rainfall radar you would like to show, and turning on _Generate animated GIF_.  For other products please see example configurations in this document.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration-image-sources.png?raw=true" />
<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration-image-generation.png?raw=true" />

On this screen you also have the option to modify the layer ordering, add additional layer, generate PNG images, generate animated GIF, change the delay between GIF images in the animated gif, enable GIF looping, enable local timestamp, configure local timestamp properties, apply post processing to the image, change image output path and output filename.

#### Image sources configuration fields

__FTP server:__
This is the BOM's FTP server.

__Images directory path:__
The location of the images sequences relative to the root directory of the FTP server.

__Image product ID:__
The product ID as described above.

__Transparencies directory path:__
The location of the static background and foreground images relative to the root directory of the FTP server.

__Regular expression for image file filter:__
Under some scenarios it is necessary to provide more specific filter by the use of regular expression.  This is an optional field.

__Date range to search:__
The date range to search.  Valid values are: `last_#d` (last # days), `last_#h` (last # hours), `last_#m` (last # minutes), `last_#s` (last # seconds), `today` and `yesterday`.  e.g `last_15m` to include files only from the last 15 minutes.  Specific start/end date is not yet supported. 

__Image layers configuration:__
The list of layers to merge.  See below for details.

__Monitoring interval in minutes:__
The interval to check for new files.

#### Image generation configuration fields

__Generate PNG images:__
Generate individual images from series.

__Generate animaged GIF:__
Generated animated GIF from series.

__GIF image delay time in ms:__
The number of milliseconds to pause before showing the next image sequence/frame.

__Enable GIF loop:__
If enabled GIF will restart when the last frame is reached.

__Embed local timestamp:__
Embed local timestamp in each image.

__Local timestamp properties:__
The timestamp's font face, font size, font weight, font decoration, font style, font colour and position configuration.

__TIFF image index:__
High-definition images, like from Himawari-8 satellite, are stored in a single TIFF image file.  There are five images in the file and each image is of varying resolution; zero being the highest resolution (6111x4167), four being lowest resolution (510x350).  The default image index is 3.  The use of image index 0 or 1 is not recommended unless your system can handle it.

__Image post-processing:__
Post processing applied to the final image.  See below for details.

__Image output path:__
The path to output the generated images.

__Image output filename:__
The name to give to the filename.  This field can also accept bind variable `${pid}`, which is the ID entered in the Product ID field.

### Image Layers Configuration

Each layer is separated by a semicolon and each setting for the layer is separated by a comma.  The order of the layer determines the layer merge order.

Each layer at minimum must contain the full image file name.  The layer where each of the series/sequence image to be merged must be named `${series}`.

For example (taken from default configuration):

`image=IDR.legend.0.png; image=${pid}.background.png; image=${pid}.topography.png; image=${series}; image=${pid}.locations.png; image=${pid}.range.png`

Explanation:
- There are six layers of images that make up the final image: legend, background, topography overlay, ${series} image, locations transparency overlay and range transparency overlay.  You can re-order the the layers to your liking.
- `${pid}` is the placeholder for product ID and gets replaced by the product ID you entered in the product ID field.  If your product ID is IDR701 then it is equivalent to type in image=IDR701.background.png as the second layer.
- Layer 1 non-transparent part of the image will be obscured by layer 2, layer 2 will be obscured by layer 3, and so on.
- Layer 3, `image=${series}`, is the placeholder for the image series. ie. the radar scan image.
- The images, except for the series images, are sourced from ftp://ftp.bom.gov.au/anon/gen/radar_transparencies/ by default (as defined in the _Transparencies directory path_).  You can specify a URL instead to include images from external sources.
- Other transparancies available from the BOM ftp site are: `${pid}.wthrDistricts.png`, `${pid}.waterways.png`, `${pid}.roads.png`, `${pid}.rail.png`, `${pid}.catchments.png`.  Including/excluding these transparencies is equivalent to toggling these feature on/off on the BOM website.
- It is possible to add image manipulation operations to each layer.  See below for more details.

Below is a list of supported external image sources:

<table>
  <tr>
    <th>URL Type</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>http</td>
    <td>HTTP protocol</td>
    <td>image=http://www.openhab.org/openhab-logo.png</td>
  </tr>
  <tr>
    <td>https</td>
    <td>Secure HTTP protocol</td>
    <td>image=https://www.openhab.org/openhab-logo.png</td>
  </tr>
  <tr>
    <td>ftp</td>
    <td>FTP protocol</td>
    <td>image=ftp://ftp.bom.gov.au/anon/gen/radar_transparencies/IDR.legend.0.png</td>
  </tr>
  <tr>
    <td>file</td>
    <td>Local file</td>
    <td>image=file:///etc/openhab2/html/location_24.png</td>
  </tr>
<table>
  
## Image Manipulation and Processing

There are five image manipulation operations available to each layer and the final image:
- Opacity - changes the opacity (transparency level) of the image.
- Resize - resizes the image.
- Crop - crops the image.
- Position - repositions image in the layer.
- Resize canvas - reizes the canvas.

<table>
<tr align="left">
  <th>Operation</th>
  <th>Parameters</th> 
  <th>Example</th> 
  <th>Notes</th>
</tr>
<tr>
  <td>opacity</td>
  <td>opacity</td>
  <td>opacity=0.5</td>
  <td>Valid values are 0.0 to 1.0.</td>
</tr>
<tr>
  <td>resize</td>
  <td>width height</td>
  <td>resize=600 600</td>
  <td>In number of pixels, must be positive numbers.</td>
</tr>
<tr>
  <td>crop</td>
  <td>x y width height</td>
  <td>crop=0 12 512 500</td>
  <td>x and y are the starting coordinate relative to top-left of the canvas. width the number of pixels from x to include.  height is the number of pixels from y to include.</td>
</tr>
<tr>
  <td>position</td>
  <td>x y</td>
  <td>position=200 148</td>
  <td></td>
</tr>
<tr>
  <td>canvas</td>
  <td>width height x y background-colour</td>
  <td>canvas=512 700 0 0 #606060</td>
  <td>x and y are the starting coordinate relative to top-left of the canvas.  If no background-colour provided the background will remain transparent.</td>
</tr>
</table>

__Example usage in a layer:__

`image=${pid}.range.png, opacity=0.5;`

`image=file:///C:/openhab2/html/location_24.png, opacity=0.5, position=218 148;`

__Example usage in Image post-processing field:__

`crop=0 12 512 500, resize=600 600`

#### Local timestamp configuration

When "Embed local timestamp" is enabled, each PNG/GIF frame will include the local timestamp in the image.  You have the option to override the default timestamp format, text properties and positioning.

By default the following properties are used:

`format=dd/MM/yyyy HH:mm:ss z, font-face=Arial, font-size=16, font-color=#080808, font-weight=bold, position=256 20`

This binding does not attempt to read the timestamp overlay in the image.  Instead the timestamp on the filename itself is used.  For some images (e.g rain radar) the timestamp on the filename does not actually match the timestamp overlay. In the case of rain radar, there is a constant 5 minute delay.  You can fix this by using `adjust-timestamp` attribute.

Available configuration properties:

<table>
  <tr>
    <th>Attribute</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>format</td>
    <td>The date format in Java.</td>
  </tr>
  <tr>
    <td>adjust-timestamp</td>
    <td>Adjust the timestamp.  Valid values: [-]#d, [-]#h, [-]#m or [-]#s.  e.g adjust-timestamp=-5m</td>
  </tr>
  <tr>
    <td>font-face</td>
    <td>The font face to use.  Default is Arial.  Fonts availability is system dependent.</td>
  </tr>
  <tr>
    <td>font-size</td>
    <td>The point size of the font.</td>
  </tr>
  <tr>
    <td>font-color</td>
    <td>The colour of the font in hexadecimal.</td>
  </tr>
  <tr>
    <td>font-weight</td>
    <td>The thickness of the font. Valid values: normal, bold.</td>
  </tr>
  <tr>
    <td>font-style</td>
    <td>The posture of the font. Valid values: normal, italic.</td>
  </tr>
  <tr>
    <td>font-decoration</td>
    <td>The decoration for the font. Valid values: none, underline.</td>
  </tr>
  <tr>
    <td>position</td>
    <td>The position of the timestamp in x and y coordinate.</td>
  </tr>
</table>

### Rain radar images configuration example

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/radar_1.png?raw=true" />

_Image directory path:_ `/anon/gen/radar/`

_Image product ID:_ `IDR701`

_Image layers configuration:_ `image=IDR.legend.0.png; image=${pid}.background.png; image=${pid}.topography.png; image=${series}; image=${pid}.locations.png; image=${pid}.range.png, opacity=0.6; image=file:///etc/openhab2/html/location_24.png, opacity=0.8, position=248 212`

_Embed local timestamp:_ `On`

_Local timestamp properties:_ `format=dd/MM/yyyy HH:mm:ss z, adjust-timestamp=-5m font-face=Arial, font-size=16, font-color=#000000, font-weight=bold, position=250 485`

__Note:__
- Timestamp is adjusted to minus 5 minutes to match the UTC time overlay.
- Opacity is added to range image overlay.
- The use of location marker with its opacity set to 0.8 and positioned to the desired location.

### Doppler wind images configuration example

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/doppler-wind.png?raw=true" />

_Image directory path:_ `/anon/gen/radar/`

_Image product ID:_ `IDR70I`

_Date range to search:_ `last_24h`

_Image layers configuration:_ `image=IDR.legend.2.png; image=IDR703.background.png; image=IDR703.topography.png; image=${series}; image=IDR703.locations.png; image=IDR703.range.png`

### Rainfall images configuration example

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/rainfall.png?raw=true" />

_Image directory path:_ `/anon/gen/radar/`

_Image product ID:_ `IDR70D`

_Image layers configuration:_ `image=IDR.legend.1.png; image=IDR703.background.png; image=${series}; 
image=IDR703.locations.png; image=IDR703.range.png`

_Embed local timestamp:_ `On`

_Local timestamp properties:_ `format=dd/MM/yyyy HH:mm:ss z, font-face=Arial, font-size=16, font-color=#080808, font-weight=bold, position=226 490`

### Satellite images configuration example

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/satellite.png?raw=true" />

Click [here](https://github.com/tomitan100/org.openhab.binding.bom/raw/master/doc/satellite.gif) for animated GIF version.

_Image directory path:_ `/anon/gen/gms/`

_Image product ID:_ `IDE00135`

_Regular expression for image filter:_ `IDE00135.\d{12}.*`

_Date range to search:_ `last_6h`

_Image layers configuration:_ `image=${series}`

_Embed local timestamp:_ `On`

_Local timestamp properties:_ `format=dd/MM/yyyy HH:mm:ss z, font-face=Arial, font-size=16, font-color=#FFFFFF, font-weight=bold, position=316 458`

__Note:__

- Regular expression is required because the product ID used to search for the image files also matches unrelated files `IDE00135.radar.*.jpg`.
- Date range is set to the past 6 hours as there are a large number of files spanning ~20 days.

#### Other satellite view variations

<table>
  <tr>
    <th>Region</th>
    <th>Satellite View</th>
    <th>Product ID</th>
    <th>Regular expression</th>
  </tr>
  <tr>
    <td>Australia</td>
    <td>False colour temperatures</td>
    <td>IDE00134</td>
    <td>IDE00134.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia</td>
    <td>Infrared, greyscale</td>
    <td>IDE00105</td>
    <td>IDE00105.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia</td>
    <td>Visible, greyscale</td>
    <td>IDE00106</td>
    <td>IDE00106.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia</td>
    <td>Infrared, Zehr enhanced</td>
    <td>IDE00133</td>
    <td>IDE00133.\d{12}.*</td>
  </tr>
  
  <tr>
    <td>Australia West</td>
    <td>False colour temperatures</td>
    <td>IDE00124</td>
    <td>IDE00124.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia West</td>
    <td>Infrared, greyscale</td>
    <td>IDE00125</td>
    <td>IDE00125.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia West</td>
    <td>Visible, greyscale</td>
    <td>IDE00126</td>
    <td>IDE00126.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia West</td>
    <td>Infrared, Zehr enhanced</td>
    <td>IDE00123</td>
    <td>IDE00123.\d{12}.*</td>
  </tr>
  
  <tr>
    <td>Australia East</td>
    <td>False colour temperatures</td>
    <td>IDE00144</td>
    <td>IDE00144.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia East</td>
    <td>Infrared, greyscale</td>
    <td>IDE00145</td>
    <td>IDE00145.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia East</td>
    <td>Visible, greyscale</td>
    <td>IDE00146</td>
    <td>IDE00146.\d{12}.*</td>
  </tr>
  <tr>
    <td>Australia East</td>
    <td>Infrared, Zehr enhanced</td>
    <td>IDE00143</td>
    <td>IDE00143.\d{12}.*</td>
  </tr>
  
  <tr>
    <td>Full Disk</td>
    <td>False colour temperatures</td>
    <td>IDE00154</td>
    <td>IDE00154.\d{12}.*</td>
  </tr>
  <tr>
    <td>Full Disk</td>
    <td>Infrared, greyscale</td>
    <td>IDE00155</td>
    <td>IDE00155.\d{12}.*</td>
  </tr>
  <tr>
    <td>Full Disk</td>
    <td>Visible, greyscale</td>
    <td>IDE00156</td>
    <td>IDE00156.\d{12}.*</td>
  </tr>
  <tr>
    <td>Full Disk</td>
    <td>Infrared, Zehr enhanced</td>
    <td>IDE00153</td>
    <td>IDE00153.\d{12}.*</td>
  </tr>
</table>  

### High-definition Himawari-8 satellite images configuration example

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/himawari.png?raw=true" />

Click [here](https://github.com/tomitan100/org.openhab.binding.bom/raw/master/doc/himwari.gif) for animated GIF version.

_Image directory path:_ `/anon/gen/gms/`

_Image product ID:_ `IDE00436`

_Date range to search:_ `last_3h`

_Image layers configuration:_ `image=${series}`

_Embed local timestamp:_ `On`

_Local timestamp properties:_ `format=dd/MM/yyyy HH:mm:ss z, font-face=Arial, font-size=16, font-color=#FFFFFF, font-weight=bold, position=510 510`

_TIFF image index:_ `3`

__Note:__

- Himawari-8 satellite images are in TIFF format and each file has 5 images of different resolution, from high (index 0) to low (index 4).  In this example index 3 is used as it provides good-enough resolution without too much overhead.  Image index 0 or 1 is not recommended unless your system can handle it.

### Mean sea-level pressure images configuration example

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/mslp.png?raw=true" />

_Image directory path:_ `/anon/gen/fwo/`

_Image product ID:_ `IDY00030`

_Regular expression for image filter:_ `IIDY00030.\d{12}.png`

_Date range to search:_ `last_7d`

_Image layers configuration:_ `image=${series}`

_Embed local timestamp:_ `On`

_Local timestamp properties:_ `format=dd/MM/yyyy HH:mm:ss z, font-face=Arial, font-size=16, font-color=#F76623, font-weight=bold, position=380 420`

__Note:__

- Regular expression is required because product ID also matches PDF files `IDY00030.*.pdf` in the FTP directory.

## Using the images

#### If you want to use generated PNG's or animated GIF

Use an Image widget and link to the generated image `/static/<output-filename>.gif` or link to the image in your custom template.

For PNG images the sequence number is appended to the image name.  i.e. `<output-filename>.<sequence>.png`.  e.g `IDR701.0.png`, `IDR701.1.png` and so on.  The list of PNG's is available in the _Generated PNG's_ channel.

#### If you choose NOT to use generate PNG's or animated GIF

In your custom template you will have to write AngularJS/Javascript to handle the display of the image layers and animating the radar images.  The benefit of this method is you can make it user interactive and frame rate, etc is not baked in. This is similar to what BOM site does and it is beyond the scope of this documentation.

The list of radar image sequences are available as a channel (Source Images).  Unfortunately it is represented as a comma-separated string due to framework shortcoming.  You will have to split the values into an array to be useful.

## Unsupported Charts

- National Radar Loop - this is made up of two series of images.  Possible to support in the future if there is demand.
- Weather and Wave Forecast Maps - this is a single file of around 154MB needs to be downloaded and processed.
- Latest Colour MSLP and Infrared Greyscale Satellite - this is a single image that can be linked directly.
- Short-term forecast - this is a single image that can be linked directly.
- Colour Forecast map for next 4 days - this is a single image that can be linked directly.
- UV Forecast - this is a single image that can be linked directly.
- Asia MSL Pressure Analysis - this is a single image that can be linked directly.
- Southern Hemisphere MSLP Aanalysis - this is a PDF file.
- Pacific Ocean MSLP Analyses - this is a single image that can be linked directly.
- Indian Ocean MSLP Analyses - this is a single image that can be linked directly.

## Example Screenshots in openHAB HABPanel

The screenshots below are examples of the binding in operation.  The screens use custom theme called "Matrix Theme" by Patrick (`@pmpkk`).  For more information about the theme please go to https://community.openhab.org/t/matrix-theme-for-habpanel/31100.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/home.png?raw=true" />

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/forecast.png?raw=true" />

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/radar-loop.gif?raw=true" />

## Change log
__18/12/2020__
- openHAB version 3.0.0 update

__20/10/2020__
- Changed FTP client to not verify remote site.

__25/09/2020__
- openHAB version 2.5.9 compatibility update.

__03/01/2020__
- Fixed retry of image generation if images are missing on the FTP site.

__23/04/2019__
- Added support for high-definition Himawari-8 satellite images (TIFF files).

__20/04/2019__
- Read timestamp from image filename instead of file.
- Added optional `adjust-timestamp` property to local timestamp configuration.
- Removed hard-coded timestamp adjustment as this is not applicable to other image products.

__18/04/2019__
- Fixed chaining of image manipulation operation.

__17/04/2019__
- Added regular expression file matching.
- Added date time range filter.

__15/04/2019__
- Added timestamp option.
- Fixed sourcing of images from external sources.

__14/04/2019__
- Added retain min/max temperatures for today.
- Added BOM radar/rainfall image generation support.

__08/03/2019__
- Fixed icon mapping for light rain.

__03/03/2019__
- Fixed apparent vs air temperature mix up.
- Fixed today's min/max temperature.

__26/02/2019__
- Added 24 hour rainfall channel.
- Added minimum and maximum precipitation.

__18/02/2019__
- Added weather station channel and additional logging.

__17/02/2019__
- Updated channel label names.

__16/02/2019__
- Added apparent temperature from observation.

__14/02/2019__
- Added support for city/town and district forecast data.
- Handled NaN values from data.

__13/02/2019__
- Updated forecast date and time label.

__12/02/2019__
- Initial release.
