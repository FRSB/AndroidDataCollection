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