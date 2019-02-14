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

At minimum there are four fields required to process the data-feed.  The FTP path to obvservation data XML, the weather station ID of observation, the FTP path to forecast data XML and the area code.

Observation FTP paths are grouped by state. Use the following table to copy the XML path of your state.

<table>
<tr align="left">
  <th>State</th>
  <th>Observation FTP Path</th> 
</tr>
<tr>
  <td><b>NSW</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN60920.xml</td>
</tr>
<tr>
  <td><b>ACT</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN60920.xml</td>
</tr>
<tr>
  <td><b>NT</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDD60920.xml</td>
</tr>
<tr>
  <td><b>QLD</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDQ60920.xml</td>
</tr>
<tr>
  <td><b>SA</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDS60920.xml</td>
</tr>
<tr>
  <td><b>TAS</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDT60920.xml</td>
</tr>
<tr>
  <td><b>VIC</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDV60920.xml</td>
</tr>
<tr>
  <td><b>WA</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDW60920.xml</td>
</tr>
</table>

The next step is to open the XML by clicking on it and locate the weather station of interest.  Copy the "wmo-id" number and use this as the station ID.

For example: "PERTH METRO" station ID in the file ftp://ftp.bom.gov.au/anon/gen/fwo/IDW60920.xml is 94608.

Next you will need to provide the FTP path to the forecast file.

Below is a list of forecast paths for Australian major cities.  If the forecast you are interested is not in the list go to this catalogue page http://reg.bom.gov.au/catalogue/anon-ftp.shtml and search for the forecast.  The type must be "Forecast". Use the Search box on the page.  Once located append the product ID to the FTP path ftp://ftp.bom.gov.au/anon/gen/fwo/ and add the ".xml" file extension.


<table>
<tr align="left">
  <th>City</th>
  <th>Forecast FTP Path</th> 
</tr>
<tr>
  <td><b>Darwin (NT)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDD10150.xml</td>
</tr>
<tr>
  <td><b>Canberra (ACT)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN10035.xml</td>
</tr>
<tr>
  <td><b>Sydney (NSW)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN10064.xml</td>
</tr>
<tr>
  <td><b>Newcastle (NSW)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN11051.xml</td>
</tr>
<tr>
  <td><b>Central Coast (NSW)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN11052.xml</td>
</tr>
<tr>
  <td><b>Wollongong (NSW)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN11053.xml</td>
</tr>
<tr>
  <td><b>Alpine Centres (NSW)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDN11055.xml</td>
</tr>
<tr>
  <td><b>Brisbane (QLD)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDQ10095.xml</td>
</tr>
<tr>
  <td><b>Gold Coast (QLD)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDQ10610.xml</td>
</tr>
<tr>
  <td><b>Sunshine Coast (QLD)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDQ10611.xml</td>
</tr>
<tr>
  <td><b>Adelaide (SA)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDS10034.xml</td>
</tr>
<tr>
  <td><b>Hobart (TAS)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDT13600.xml</td>
</tr>
<tr>
  <td><b>Launceston (TAS)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDT13610.xml</td>
</tr>
<tr>
  <td><b>Melbourne (VIC)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDV10450.xml</td>
</tr>
<tr>
  <td><b>Geelong (VIC)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDV10701.xml</td>
</tr>
<tr>
  <td><b>Mornington Peninsula (VIC)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDV10702.xml</td>
</tr>
<tr>
  <td><b>Perth (WA)</b></td>
  <td>ftp://ftp.bom.gov.au/anon/gen/fwo/IDW12300.xml</td>
</tr>
</table>

Once you have located the forecast data-feed open the file and locate the area code (aac) you would like to use.

For example: Perth's aac code in ftp://ftp.bom.gov.au/anon/gen/fwo/IDW12300.xml is "WA_PT053".

For more information about data-feeds, please go to http://reg.bom.gov.au/catalogue/data-feeds.shtml

Screenshot below shows the binding configuration screen in Paper UI.

<img src="https://github.com/tomitan100/org.openhab.binding.bom/blob/master/doc/configuration.png?raw=true" />


## Forecast Icons

The following table show all the possible icon names returned by the channel.

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

