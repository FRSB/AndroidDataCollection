rm(list = ls())

# install required libraries and packages
.libPaths("lib") #remove this line to use your personal library instead
#install.packages("hash")

# load required libraries and packages
source("MarkovChains.R")
source("DataTransformation.R")
source("Evaluation.R")
source("Locations.R")
library(hash)

# generate dummy data set
#cellIds = c(1,1,1,1,1,1,1,2,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4)
#cells = rep("cell", length(cellIds))
#cells = paste(cells, cellIds)

# read real data
data = read.csv("olddata.csv", sep=";")
cells = data$cellid

# plot cell locations
cellLocations = estimateCellLocations(data)
plot(x=cellLocations$latitude, y=cellLocations$longitude)

removeInfrequentCells = function(cells, threshold=1) {
  frequentCells = names(which(table(cells)>threshold))
  cells = cells[cells %in% frequentCells]
  cells = removeDuplicateConsecutiveStates(cells)
  return(cells)
}

# data transformation
cells = removeDuplicateConsecutiveStates(cells)
cells = removeInfrequentCells(cells)
cellData = encodeCells(cells)
cellIds = cellData[[1]]
windowedCellIds = applyWindow(cellIds)

# infer dummy data
numStates = length(unique(c(windowedCellIds$tNext,windowedCellIds[1,])))
t1 = FirstOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
t2 = SecondOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
#t3 = ThirdOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)

# apply models on dummy data 
p1 = FirstOrderMarkovChain.predictStates(t1,windowedCellIds)
p2 = SecondOrderMarkovChain.predictStates(t2,windowedCellIds)
#p3 = ThirdOrderMarkovChain.predictStates(t3,windowedCellIds)

# count number of right predictions
calculateAccuracy(p1)
calculateAccuracy(p2)
#calculateAccuracy(p3)

# apply cross validation
applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=FirstOrderMarkovChain.inferTransitionTensor, predictor=FirstOrderMarkovChain.predictStates, evaluator=calculateAccuracy)
applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=SecondOrderMarkovChain.inferTransitionTensor, predictor=SecondOrderMarkovChain.predictStates, evaluator=calculateAccuracy)
applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=ThirdOrderMarkovChain.inferTransitionTensor, predictor=ThirdOrderMarkovChain.predictStates, evaluator=calculateAccuracy)