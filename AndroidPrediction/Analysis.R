rm(list = ls())

# install required libraries and packages
.libPaths("lib") #remove this line to use your personal library instead
#install.packages("hash")

# load required libraries and packages
source("MarkovChains.R")
source("DataTransformation.R")
library(hash)

# Generate dummy data set
cellIds = c(1,2,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4,1,2,3,4,1,3,4,1,2,4,1,4,1,2,3,1,2,3,4)
cells = rep("cell", length(cellIds))
data = paste(cells, cellIds)

# data transformation (cell -> cellId)
# input: data, output: data, cellNameToCellId, cellIdToCellName
states = unique(data)
cellNameToCellId = hash(keys=states, values=1:length(states))
cellIdToCellName = vector(length=length(states))
i = 1
for (state in states) {
  cellIdToCellName[i] = state
  i = i + 1
}
for (i in 1:length(data)) {
  cellIds[i] = cellNameToCellId[[as.character(data[i])]]
}
data=cellIds

# infer dummy data
t1 = FirstOrderMarkovChain.inferTransitionTensor(data)
t2 = SecondOrderMarkovChain.inferTransitionTensor(data)

# apply models on dummy data 
p1 = applyFirstOrderMarkovChain(t1,data)
p2 = applySecondOrderMarkovChain(t2,data)

# count number of right predictions
dim(p1[p1$tNext==p1$prediction,])[1]/dim(p1)[1]
dim(p2[p2$tNext==p2$prediction,])[1]/dim(p2)[1]