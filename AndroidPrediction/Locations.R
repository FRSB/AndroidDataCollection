# estimate cell locations given the app csv
# in:   - data, must contain $cellid, $gpsaccuracy, $gpslatitude, $gpslongitude
# out:  - cellLocations, containing cellId, latitude and longitude
estimateCellLocations = function(data) {
  gpsData = data.frame(data$cellid, data$gpsaccuracy, data$gpslatitude, data$gpslongitude)
  names(gpsData) = c("cell", "acc", "lat", "lon")
  gpsData = gpsData[!(is.na(gpsData$acc)),]
  gpsData = gpsData[!(is.na(gpsData$lat)),]
  gpsData = gpsData[!(is.na(gpsData$lon)),]
  avgLat = aggregate(gpsData$lat, by=list(cell=gpsData$cell), FUN=mean)
  avgLon = aggregate(gpsData$lon, by=list(cell=gpsData$cell), FUN=mean)
  cellLocations = data.frame(avgLat$cell, avgLat$x, avgLon$x)
  names(cellLocations) = c("cellId", "latitude", "longitude")
  return(cellLocations)
}
