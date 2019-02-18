# <bindingName> Australian Bureau of Meteorology Weather Forecast Binding

This Eclipse Smarthome/openHAB binding allows retrieval of Australian weather forecast from Bureau of Meteorology.

## Features

This initial release maps most fields from BOM data-feed.

For today's observation and forecast these fields are available:

- Observation time
- Date and time of forecast
- Forecast icon name
- Precis (i.e. abstract)
- Forecast text
- Minimum temperature
- Maximum temperature
- Possibility of precipitation
- UV alert text
- Current air temperature
- Dew point
- Relative humidity
- Atmospheric pressure
- Wind direction
- Wind direction in degrees
- Wind speed in km/h
- Wind speed in Knots
- Rainfall

For future forecasts the following fields are available:

- Date and time of forecast
- Forecast icon name
- Precis (i.e. abstract)
- Forecast text
- Minimum temperature
- Maximum temperature
- Possibility of precipitation
- UV alert text

## Installation

For openHAB install "Eclipse IoT Market" add-on under MISC tab in openHAB Paper UI.  Then install "Australian BOM Weather Forecast Binding" from the Bindings page.

For Eclipse SmartHome install from https://marketplace.eclipse.org/content/australian-bom-weather-forecast-binding.

## Configuration

At minimum there are five fields required to process the data-feed.  The observation product ID, the weather station ID of observation, the precis product ID, the city/town product ID and finally the area code.

Listed below is the observation product ID for your state. Enter the ID you need into the "Observation product ID" field in Paper UI things configuration.

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

The next step is to open the product XML by loading ftp://ftp.bom.gov.au/anon/gen/fwo/{product-id}.xml in your browser and locate the weather station of interest.  Copy the "wmo-id" number and use this as the "Weather station ID".

For example: "PERTH METRO" station ID in the file ftp://ftp.bom.gov.au/anon/gen/fwo/IDW60920.xml is 94608.

Next you will need to provide the precis forecast product ID and city/town/district forecast product ID.

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

NOTE: If the forecast product ID's you are after is not in the list go to this catalogue page http://reg.bom.gov.au/catalogue/anon-ftp.shtml and search for the products.  The type must be "Forecast". Use the Search box on the page by entering, e.g "(WA)", or something more specific like "City Forecast":

1. Locate "Precis Forecast XML Package ({your-state})" and enter the product ID into the field "Precis forecast product ID" back in Paper UI Thing configuration.
2. Locate "City Forecast - {your-city} ({your-state})" for city forecasts OR "Town Forecast - {your-town} ({your-state})" for town forecasts OR "District Forecast - {your-district} ({your-state})" for district forecasts.  Enter the product ID into the configuration field "City/town/district forecast product ID" in Paper UI.

Now open either the precis or the city/town/district forecast XML (ftp://ftp.bom.gov.au/anon/gen/fwo/{product-id}.xml) and locate the area code (aac code).

For example: Perth's aac code in ftp://ftp.bom.gov.au/anon/gen/fwo/IDW12300.xml is "WA_PT053".

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

## Example Screenshots in openHAB HABPanel

The screenshots below are examples of the binding in operation.  The screens use custom theme called "Matrix Theme" by Patrick (pmpkk).  For more information about the theme please go to https://community.openhab.org/t/matrix-theme-for-habpanel/31100.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/home.png?raw=true" />

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/forecast.png?raw=true" />

