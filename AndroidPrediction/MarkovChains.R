# infer transition matrix from state sequence
# one state per row, states have to be integers beginning from 1
FirstOrderMarkovChain.inferTransitionMatrix = function(data) {
  numStates = length(levels(as.factor(data[,1])))
  transitionMatrix = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
  for (i in 2:length(data[,1])) {
    transitionMatrix[data[,1][i-1],data[,1][i]] = transitionMatrix[data[,1][i-1],data[,1][i]] + 1
  }
  stateSums = apply(transitionMatrix,1,sum)
  zeroRows=which(stateSums==0)
  stateSums[stateSums==0]=-1
  transitionMatrix = transitionMatrix / stateSums
  transitionMatrix[zeroRows,]=1/numStates
  return(transitionMatrix)
}

# sample markov chain from transition matrix
# beginning from initial state
FirstOrderMarkovChain.sampleFromTransitionMatrix = function(transitionMatrix, initialState, length) {
  numStates = dim(transitionMatrix)[1]
  samples = vector(length=length)
  samples[1] = initialState
  for (i in 2:length) {
    samples[i] = sample(1:numStates, size=1, replace=TRUE, prob=transitionMatrix[samples[i-1],])
  }
  return(samples)
}

# predict next state given a current state
# by simply chosing the one with the highest transition probability
FirstOrderMarkovChain.predictNextState = function(transitionMatrix, currentState) {
  return(which.max(transitionMatrix[currentState,]))
}

# infer transition tensor from state sequence
# one state per row, states have to be integers beginning from 1
SecondOrderMarkovChain.inferTransitionTensor = function(data) {
  numStates = length(levels(as.factor(data[,1])))
  stateSums = matrix(1, nrow=numStates, ncol=numStates) #1 forlaplace correction
  transitionTensor = list()
  for (i in 1:numStates) {
    transitionTensor[[i]] = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
  }
  for (i in 3:length(data[,1])) {
    transitionTensor[[data[,1][i]]][data[,1][i-2],data[,1][i-1]] =
      transitionTensor[[data[,1][i]]][data[,1][i-2],data[,1][i-1]] + 1
    stateSums[data[,1][i-2],data[,1][i-1]] = 
      stateSums[data[,1][i-2],data[,1][i-1]] + 1
  }
  for (i in 1:numStates) {
    transitionTensor[[i]] = transitionTensor[[i]] / stateSums
  }
  return(transitionTensor)
}

# sample markov chain from transition tensor
# beginning from initial states c(state1, state2)
SecondOrderMarkovChain.sampleFromTransitionTensor = function(transitionTensor, initialStates, length) {
  numStates = length(transitionTensor)
  samples = vector(length=length)
  samples[1] = initialStates[1]
  samples[2] = initialStates[2]
  transitionVector = vector(length=numStates)
  for (i in 3:length) {
    for (j in 1:numStates) {
      transitionVector[j] = transitionTensor[[j]][samples[i-2],samples[i-1]]
    }
    samples[i] = sample(1:numStates, size=1, replace=TRUE, prob=transitionVector)
  }
  return(samples)
}

# predict next state given the current and last but current state
# by simply chosing the one with the highest transition probability
SecondOrderMarkovChain.predictNextState = function(transitionTensor, currentStates) {
  numStates = length(transitionTensor)
  transitionVector = vector(length=numStates)
  for (i in 1:numStates) {
    transitionVector[i] = transitionTensor[[i]][currentStates[1],currentStates[2]]
  }
  return(which.max(transitionVector))
}

