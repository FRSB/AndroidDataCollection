# calculate classification accuracy
# in:   - predictedData, windowed state sequence with predictions
#         predictions have to be in predictedData$tPred
# out:  - accuracy
calculateAccuracy = function(predictedData) {
  return(dim(predictedData[predictedData$tNext==predictedData$tPred,])[1]/dim(predictedData)[1])
}