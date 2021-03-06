/**
 *  Curb Power Meter
 *
 *  Copyright 2016 Neil Zumwalde
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Curb Power Meter", namespace: "curb-v2", author: "Neil Zumwalde") {
		capability "Power Meter"
		capability "Sensor"
        capability "Energy Meter"
        capability "Switch"
	}

	simulator { }

	tiles
    {
		multiAttributeTile(name:"power", type: "lighting", width: 2, height: 2, canChangeIcon: false) {
        	tileAttribute ("device.power", key: "PRIMARY_CONTROL") {
 	    		attributeState "power",
                label:'${currentValue} W',
                icon:'st.switches.switch.off',
                backgroundColors: [
								[value: -1000,   color: "#25c100"],
								[value: -500,   color: "#76ce61"],
					      		[value: -100,   color: "#bbedaf"],
            					[value: 0,   color: "#bcbbb5"],
            					[value: 100, color: "#efc621"],
                				[value: 1000, color: "#ed8c25"],
								[value: 2000, color: "#db5e1f"]
        		]
			}
            tileAttribute ("device.energy", key: "SECONDARY_CONTROL") {
 	    		attributeState "kwhr", label:'${currentValue} kWh'
			}
        }
         standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: false) {
            state "off", label: '${currentValue}', icon:'st.switches.switch.off', backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', icon:'st.switches.switch.on', backgroundColor: "#00a0dc"
        }
		htmlTile(name:"graph",
				 action: "generateGraph",
				 refreshInterval: 10,
				 width: 6, height: 6,
				 whitelist: ["www.gstatic.com"])


		main (["power"])
		details(["power", "switch", "graph"])
	}
}

mappings {
	path("/generateGraph") {action: [GET: "generateGraphHTML"]}
}

def handleMeasurements(values)
{
	if(values instanceof Collection)
    {
    	// For some reason they show up out of order
    	values.sort{a,b -> a.t <=> b.t}
		state.values = values;
    }
    else
    {
    	sendEvent(name: "power", value: Math.round(values))

        if(Math.round(values) > Math.round(state.threshold)){
        	sendEvent(name: "switch", value: "on")
        } else {
        	sendEvent(name: "switch", value: "off")
        }
    }

    //log.debug("State now contains ${state.toString().length()}/100000 bytes")
}
def handleKwhr(kwhr)
{
	sendEvent(name: "energy", value: Math.round(kwhr))
}

def setAggregate(agg)
{
	def threshold = Math.round(agg.avg) + (0.5 * (Math.round(agg.max) - Math.round(agg.avg)))
    if ( threshold > (Math.round(agg.max) * 0.95))
    {
    	threshold = Math.round(agg.max) * 0.90
    }
    if (threshold < 50){
    	threshold = 50
    }
    state.threshold = threshold
}

String getDataString()
{
	def dataString = ""

	state.values.each()
    {
    	def ts = (long)it.t * 1000.0
		dataString += ["new Date(${ts})", it.w].toString() + ","
	}
    //log.debug "dataString: ${dataString}"

	return dataString
}

def generateGraphHTML() {
	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
					<script type="text/javascript">
						google.charts.load('current', {packages: ['corechart']});
						google.charts.setOnLoadCallback(drawGraph);
						function drawGraph() {
							var data = new google.visualization.DataTable();
							data.addColumn('datetime', 'time');
							data.addColumn('number', 'Power');
							data.addRows([
								${getDataString()}
							]);
							var options = {
								fontName: 'San Francisco, Roboto, Arial',
								height: 240,
								hAxis: {
									format: 'h:mm aa',
									slantedText: false
								},
								series: {
									0: {targetAxisIndex: 0, color: '#FFC2C2', lineWidth: 1}
								},
								vAxes: {
									0: {
										title: 'Power (W)',
										format: 'decimal',
										textStyle: {color: '#004CFF'},
										titleTextStyle: {color: '#004CFF'},
                                        minValue: 0
									},
								},
								legend: {
									position: 'none'
								},
								chartArea: {
									width: '72%',
									height: '85%'
								}
							};
							var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));
							chart.draw(data, options);
						}
					</script>
				</head>
				<body>
					<div id="chart_div"></div>
				</body>
			</html>
		"""
	render contentType: "text/html", data: html, status: 200
}
