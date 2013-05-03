rm(list = ls())

# install required libraries and packages
.libPaths("lib") #remove this line to use your personal library instead
#install.packages("hash")

# load required libraries and packages
source("MarkovChains.R")
source("DataTransformation.R")
source("Evaluation.R")
library(hash)

# generate dummy data set
cellIds = c(1,1,1,1,1,1,1,2,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4)
cells = rep("cell", length(cellIds))
cells = paste(cells, cellIds)

# read real data
#data = read.csv("testdata.csv", sep=";")
#cells = data$cellid

# data transformation
cellData = encodeCells(cells)
cellIds = cellData[[1]]
cellIds = removeDuplicateConsecutiveStates(cellIds)
windowedCellIds = applyWindow(cellIds)

# infer dummy data
t1 = FirstOrderMarkovChain.inferTransitionTensor(windowedCellIds)
t2 = SecondOrderMarkovChain.inferTransitionTensor(windowedCellIds)

# apply models on dummy data 
p1 = FirstOrderMarkovChain.predictStates(t1,windowedCellIds)
p2 = SecondOrderMarkovChain.predictStates(t2,windowedCellIds)

# count number of right predictions
calculateAccuracy(p1)
calculateAccuracy(p2)

# apply cross validation
applyNFoldCrossValidation(n=10, method="random", data=windowedCellIds, inferencer=FirstOrderMarkovChain.inferTransitionTensor, predictor=FirstOrderMarkovChain.predictStates, evaluator=calculateAccuracy)