# apply a sliding window (size 3) on a data sequence
# one state per row, states have to be integers beginning from 1
# in:   - cellIds, vector of cell ids
# out:  - dataframe with columns (t3, t2, t1, tNext) in chronological order
applyWindow = function(cellIds) {
  tNext = vector(length = length(cellIds)-3)
  t1 = vector(length = length(cellIds)-3)
  t2 = vector(length = length(cellIds)-3)
  t3 = vector(length = length(cellIds)-3)
  for (i in 4:length(cellIds)) {
    tNext[i-3] = cellIds[i]
    t1[i-3] = cellIds[i-1]
    t2[i-3] = cellIds[i-2]
    t3[i-3] = cellIds[i-3]
  }
  return(data.frame(cbind(t3,t2,t1,tNext)))
}

# encode cell names to integers beginning from 1
# in:   - data, vector of observations
# out:  - list consisting of
#         [[1]] cellIds
#         [[2]] cellNameToCellId (hashmap)
#         [[3]] cellIdTOCellName (vector)
encodeCells = function(data) {
  result = list()
  states = unique(data)
  cellIds = vector()
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
  result[[1]] = cellIds
  result[[2]] = cellNameToCellId
  result[[3]] = cellIdToCellName
  return(result)
}

# removes duplicate consecutive states from a cellId sequence
# 1,2,3,3,1,1,4 gives 1,2,3,1,4
# in:   - cellIds, vector of cell ids
# out:  - cellIds, vector of cell ids without duplicate consecutive states
removeDuplicateConsecutiveStates = function(cellIds) {
  result = cellIds[1]
  for (i in 2:length(cellIds)) {
    if (result[length(result)] != cellIds[i]) {
      result = c(result,cellIds[i]) 
    }
  }
  return(result)
}

removeInfrequentCells = function(cells, threshold=1) {
  frequentCells = names(which(table(cells)>threshold))
  cells = cells[cells %in% frequentCells]
  cells = removeDuplicateConsecutiveStates(cells)
  return(cells)
}

# splits windowed data set into test and training data
# this is used by the cross validation procedure
# in:   - data, windowed sequence of cell ids
#       - slices, number of slices (5 to 10 is working good)
#       - testSlice, index of the slice that will be test data
# out:  - list of training data and test data
#         result[[1]] = trainingData
#         result[[2]] = testData
splitData = function(data, slices, testSlice) {
  size = dim(data)[1]
  sliceSize = round(size/slices)
  if (testSlice == slices) {
    testData = data[(sliceSize*(slices-1)+1):size,]
    trainingData = data[-((sliceSize*(slices-1)+1):size),]
  } else {
    testData = data[((testSlice-1)*sliceSize+1):(testSlice*sliceSize),]
    trainingData = data[-(((testSlice-1)*sliceSize+1):(testSlice*sliceSize)),]
  }
  result = list()
  result[[1]] = trainingData
  result[[2]] = testData
  return(result)
}