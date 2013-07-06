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
data = read.csv("pp_sb_data.csv", sep=";")
cells = data$cellid

# plot cell locations
cellLocations = estimateCellLocations(data)
pdf("cell_locations.pdf",title="Cell Locations",width=12)
plot(y=cellLocations$latitude, x=cellLocations$longitude,ylab="Latitude",xlab="Longitude", type="p", pch=16, xaxt="n")
rect(par("usr")[1], par("usr")[3], par("usr")[2], par("usr")[4], col = "lemonchiffon1")
points(y=cellLocations$latitude, x=cellLocations$longitude, pch=16)
axis(1, seq(10,13,0.2))
par(xaxp = c(10,13,15))
grid(col="gray35")
dev.off()

# data transformation
cells = removeNA(cells)
cells = removeDuplicateConsecutiveStates(cells)
#cells = removeInfrequentCells(cells)
cellData = encodeCells(cells)
cellIds = cellData[[1]]
windowedCellIds = applyWindow(cellIds)

# apply growing window evaluation
a1=applyGrowingWindowValidation(data=windowedCellIds, inferencer=FirstOrderMarkovChain.inferTransitionTensor, predictor=FirstOrderMarkovChain.predictStates, evaluator=calculateAccuracy, warmUp=1)
a2=applyGrowingWindowValidation(data=windowedCellIds, inferencer=SecondOrderMarkovChain.inferTransitionTensor, predictor=SecondOrderMarkovChain.predictStates, evaluator=calculateAccuracy, warmUp=1)
a3=applyGrowingWindowValidation(data=windowedCellIds[1:nrow(windowedCellIds)], inferencer=ThirdOrderMarkovChain.inferTransitionTensor, predictor=ThirdOrderMarkovChain.predictStates, evaluator=calculateAccuracy, warmUp=1)
#a4=applyGrowingWindowValidation(data=windowedCellIds, inferencer=HiddenMarkovModel.infer, predictor=HiddenMarkovModel.predictStates, evaluator=calculateAccuracy, warmUp=1)

pdf("prediction_accuracy.pdf", title="Prediction Accuracy", width=9)
plot(a1, type="l", col="blue", xlab="Anzahl Beobachtungen", ylab="Kumulierte Vorhersagegenauigkeit", ylim=c(0,0.5), lwd=2)
lines(a2, col="darkmagenta", lwd=2)
lines(a3, col="darkgreen", lwd=2)
legend(440, 0.1, c("MK 1. Ordnung", "MK 2. Ordnung", "MK 3. Ordnung"), lty=c(1,1), lwd=c(2,2), col=c("blue", "darkmagenta", "darkgreen"))
dev.off()
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
