# Apply a sliding window (size 3) on a data sequence
# one state per row, states have to be integers beginning from 1
# in:   - cellIds, vector of observations
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