# calculate classification accuracy
# in:   - predictedData, windowed state sequence with predictions
#         predictions have to be in predictedData$tPred
# out:  - accuracy
calculateAccuracy = function(predictedData) {
  return(dim(predictedData[predictedData$tNext==predictedData$tPred,])[1]/dim(predictedData)[1])
}

# apply n times n fold cross validation
# example:  applyNFoldCrossValidation(data=windowedCellIds,
#             inferencer=FirstOrderMarkovChain.inferTransitionTensor,
#             predictor=FirstOrderMarkovChain.predictStates,
#             evaluator=calculateAccuracy)
# in:   - data, windowed sequence of cell ids
#       - inferencer, function to infer transition tensor (model) from
#       - predictor, function that applies the model to a data set
#       - evaluator, function that calculates a performance measure based on
#         the predictions (tNext vs. tPred)
#       - n, number of times and number of slices
#       - method, "random" or "linear" for random or linear sampling
# out:  - averaged evaluation measure over n times cross validations
applyNFoldCrossValidation = function(data, inferencer, predictor, evaluator, n=10, method="random") {
  if (method == "random") {
    data = data[sample(nrow(data)),]
  } else if (method == "linear") {
    
  } else {
    stop(paste("method unknown:", method, "\n", "available options: {linear, random}"))
  }
  averageAccuracy = 0
  for (i in 1:n) {
    splits = splitData(data,slices=n,testSlice=i)
    trainingData = splits[[1]]
    testData = splits[[2]]
    model = inferencer(trainingData)
    predictions = predictor(model, testData)
    averageAccuracy = averageAccuracy + evaluator(predictions)
  }
  averageAccuracy = averageAccuracy / n
  return(averageAccuracy)
}