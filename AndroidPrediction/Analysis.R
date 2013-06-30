rm(list = ls())

# install required libraries and packages
.libPaths("lib") #remove this line to use your personal library instead
#install.packages("hash")
#install.packages("HMM")

# load required libraries and packages
source("MarkovChains.R")
source("DataTransformation.R")
source("Evaluation.R")
source("Locations.R")
library(hash)
library(HMM)

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

# infer dummy data
numStates = length(unique(c(windowedCellIds$tNext,windowedCellIds[1,])))
t1 = FirstOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
t2 = SecondOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
#t3 = ThirdOrderMarkovChain.inferTransitionTensor(windowedCellIds, numStates)
hmm = HiddenMarkovModel.infer(windowedCellIds, numStates)

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

HiddenMarkovModel.infer = function(data, numStates) {
  cellIds = c(data$t3, data$t2, data$t1, data$tNext)
  uniqueCellIds = 1:numStates
  hmmStates = c("A", "B", "C", "D")
  
  transProbs = runif(length(hmmStates)**2,0,100)
  transProbs = matrix(transProbs, nrow=length(hmmStates), ncol=length(hmmStates))
  transProbs = sweep(transProbs, 1, rowSums(transProbs), FUN="/")
  emissionProbs = runif(length(uniqueCellIds)*length(hmmStates),0,100)
  emissionProbs = matrix(emissionProbs, nrow=length(hmmStates), ncol=length(uniqueCellIds))
  emissionProbs = sweep(emissionProbs, 1, rowSums(emissionProbs), FUN="/")
  
  hmm = initHMM(States=hmmStates,Symbols=uniqueCellIds,transProbs=transProbs, emissionProbs=emissionProbs)
  bw = baumWelch(hmm,cellIds,10)
  return(bw$hmm)
}

HiddenMarkovModel.sample = function(model, initialState, length) {
  return(simHMM(model, length)$observation)
}

HiddenMarkovModel.predictNextState = function(model, currentState) {
  hiddenState = which.max(model$emissionProbs[,currentState])
  nextHiddenState = which.max(model$transProbs[hiddenState,])
  nextEmission = which.max(model$emissionProbs[nextHiddenState,])
  return(nextEmission)
}
