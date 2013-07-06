rm(list = ls())

# install required libraries and packages
.libPaths("lib") #remove this line to use your personal library instead
#install.packages("hash")
#install.packages("HMM")

# load required libraries and packages
library(hash)
library(HMM)
source("MarkovChains.R")
source("DataTransformation.R")
source("Evaluation.R")
source("Locations.R")

# generate dummy data set
cellIds = c(1,1,1,1,1,1,1,2,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4)
cells = rep("cell", length(cellIds))
cells = paste(cells, cellIds)

# read real data
data = read.csv("olddata.csv", sep=";")
cells = data$cellid

# plot cell locations
cellLocations = estimateCellLocations(data)
pdf("cell_locations.pdf",title="Cell Locations")
plot(y=cellLocations$latitude, x=cellLocations$longitude,ylab="Latitude",xlab="Longitude", type="p", pch=16)
grid(col="gray35")
dev.off()

# data transformation
cells = removeDuplicateConsecutiveStates(cells)
cells = removeInfrequentCells(cells)
cellData = encodeCells(cells)
cellIds = cellData[[1]]
windowedCellIds = applyWindow(cellIds)

# apply growing window evaluation
a1=applyGrowingWindowValidation(data=windowedCellIds, inferencer=FirstOrderMarkovChain.inferTransitionTensor, predictor=FirstOrderMarkovChain.predictStates, evaluator=calculateAccuracy)
a2=applyGrowingWindowValidation(data=windowedCellIds, inferencer=SecondOrderMarkovChain.inferTransitionTensor, predictor=SecondOrderMarkovChain.predictStates, evaluator=calculateAccuracy)

# infer dummy data
#numStates = length(unique(c(windowedCellIds$tNext,windowedCellIds[1,])))
#t1 = FirstOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
#t2 = SecondOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
#t3 = ThirdOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
#tHmm = HiddenMarkovModel.infer(windowedCellIds, numStates)

# apply models on dummy data 
#p1 = FirstOrderMarkovChain.predictStates(t1,windowedCellIds)
#p2 = SecondOrderMarkovChain.predictStates(t2,windowedCellIds)
#p3 = ThirdOrderMarkovChain.predictStates(t3,windowedCellIds)
#pHmm = HiddenMarkovModel.predictStates(tHmm,windowedCellIds)

# count number of right predictions
#calculateAccuracy(p1)
#calculateAccuracy(p2)
#calculateAccuracy(p3)
#calculateAccuracy(pHmm)

# apply cross validation
#applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=FirstOrderMarkovChain.inferTransitionTensor, predictor=FirstOrderMarkovChain.predictStates, evaluator=calculateAccuracy)
#applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=SecondOrderMarkovChain.inferTransitionTensor, predictor=SecondOrderMarkovChain.predictStates, evaluator=calculateAccuracy)
#applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=ThirdOrderMarkovChain.inferTransitionTensor, predictor=ThirdOrderMarkovChain.predictStates, evaluator=calculateAccuracy)
#applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=HiddenMarkovModel.infer, predictor=HiddenMarkovModel.predictStates, evaluator=calculateAccuracy)
