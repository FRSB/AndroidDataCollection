#####################################################################################################
#                                                                                                   #
#   Implementation of Markov chains. Inference is only done for the transition probabilities        #
#   and not the initial probabilities. We use ML with laplace correction for parameter estimation.  #
#                                                                                                   #
#   Authors: Frank Rosner, Sebastian Baer                                                           #
#                                                                                                   #
#####################################################################################################

# First order Markov chain
##########################

# Infer transition matrix from vector of states (observation sequence)
# one state per row, states have to be integers beginning from 1
# in:   - data, vector of observations
# out:  - transitionTensor containing the transition probabilities of 1st order markov chain
FirstOrderMarkovChain.inferTransitionTensor = function(data) {
  numStates = length(unique(data))
  transitionMatrix = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
  for (i in 2:length(data)) {
    transitionMatrix[data[i-1],data[i]] = transitionMatrix[data[i-1],data[i]] + 1
  }
  stateSums = apply(transitionMatrix,1,sum)
  zeroRows=which(stateSums==0)
  stateSums[stateSums==0]=-1
  transitionMatrix = transitionMatrix / stateSums
  transitionMatrix[zeroRows,]=1/numStates
  return(transitionMatrix)
}

# sample random walk from transition matrix
# beginning from initial state
# in:   - transitionTensor, either infered or given transition probabilities of 1st order markov chain
#       - initialState, single integer noting the initial state to begin sampling with
#       - length, length of random walk
# out:  - random walk as state sequence
FirstOrderMarkovChain.sampleFromTransitionTensor = function(transitionTensor, initialState, length) {
  numStates = dim(transitionTensor)[1]
  samples = vector(length=length)
  samples[1] = initialState
  for (i in 2:length) {
    samples[i] = sample(1:numStates, size=1, replace=TRUE, prob=transitionTensor[samples[i-1],])
  }
  return(samples)
}

# predict next state given a current state
# by simply chosing the one with the highest transition probability
# in:   - transitionTensor, either infered or given transition probabilities of 1st order markov chain
#       - currentState, single integer noting the current state to predict from
# out:  - next state (integer) with highest transition probability
FirstOrderMarkovChain.predictNextState = function(transitionTensor, currentState) {
  return(which.max(transitionTensor[currentState,]))
}

# Second order Markov chain
###########################

# Infer transition matrix from vector of states (observation sequence)
# one state per row, states have to be integers beginning from 1
# in:   - data, vector of observations
# out:  - transitionTensor containing the transition probabilities of 2nd order markov chain
SecondOrderMarkovChain.inferTransitionTensor = function(data) {
  numStates = length(unique(data))
  stateSums = matrix(numStates, nrow=numStates, ncol=numStates) #1 forlaplace correction
  transitionTensor = list()
  for (i in 1:numStates) {
    transitionTensor[[i]] = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
  }
  for (i in 3:length(data)) {
    transitionTensor[[data[i]]][data[i-2],data[i-1]] =
      transitionTensor[[data[i]]][data[i-2],data[i-1]] + 1
    stateSums[data[i-2],data[i-1]] = 
      stateSums[data[i-2],data[i-1]] + 1
  }
  for (i in 1:numStates) {
    transitionTensor[[i]] = transitionTensor[[i]] / stateSums
  }
  return(transitionTensor)
}

# sample markov chain from transition tensor
# beginning from initial states c(state1, state2)
# in:   - transitionTensor, either infered or given transition probabilities of 2nd order markov chain
#       - initialState, a vector containing two integers noting the initial states to begin sampling with
#       - length, length of random walk
# out:  - random walk as state sequence
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
# in:   - transitionTensor, either infered or given transition probabilities of 2nd order markov chain
#       - currentState, a vector containing two integers noting the current and last but current 
#         states to predict from
# out:  - next state (integer) with highest transition probability
SecondOrderMarkovChain.predictNextState = function(transitionTensor, currentStates) {
  numStates = length(transitionTensor)
  transitionVector = vector(length=numStates)
  for (i in 1:numStates) {
    transitionVector[i] = transitionTensor[[i]][currentStates[1],currentStates[2]]
  }
  return(which.max(transitionVector))
}