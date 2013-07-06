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

# infer transition matrix windowed vector of states
# in:   - data, windowed observation sequence (names = t3, t2, t1, tNext)
# out:  - transitionTensor containing the transition probabilities of 1st order markov chain
FirstOrderMarkovChain.inferTransitionTensor = function(data, numStates) {
  transitionMatrix = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
  for (i in 1:dim(data)[1]) {
    transitionMatrix[data$t1[i],data$tNext[i]] = transitionMatrix[data$t1[i],data$tNext[i]] + 1
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

# apply prediction to windowed test data
# in:   - transitionTensor, either infered or given transition probabilities of 1st order markov chain
#       - data, windowed test data (only t3, t2 and t1 are used)
# out:  - windowed data with added column "tPred"
FirstOrderMarkovChain.predictStates = function(transitionTensor, data) {
  tPred = vector(length = length(data)-3)
  for (i in 1:dim(data)[1]) {
    tPred[i] = FirstOrderMarkovChain.predictNextState(transitionTensor, data$t1[i])
  }
  result = data.frame(data, tPred)
  return (result)
}

# Second order Markov chain
###########################

# Infer transition matrix from windowed state sequence
# in:   - data, windowed observation sequence (names = t3, t2, t1, tNext)
# out:  - transitionTensor containing the transition probabilities of 2nd order markov chain
SecondOrderMarkovChain.inferTransitionTensor = function(data, numStates) {
  stateSums = matrix(numStates, nrow=numStates, ncol=numStates) #numStates for sum of 1 (laplace correction)
  transitionTensor = list()
  for (i in 1:numStates) {
    transitionTensor[[i]] = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
  }
  for (i in 1:dim(data)[1]) {
    transitionTensor[[data$tNext[i]]][data$t2[i],data$t1[i]] =
      transitionTensor[[data$tNext[i]]][data$t2[i],data$t1[i]] + 1
    stateSums[data$t2[i],data$t1[i]] = 
      stateSums[data$t2[i],data$t1[i]] + 1
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

# apply prediction to windowed test data
# in:   - transitionTensor, either infered or given transition probabilities of 2nd order markov chain
#       - data, windowed test data (only t3, t2 and t1 are used)
# out:  - windowed data with added column "tPred"
SecondOrderMarkovChain.predictStates = function(transitionTensor, data) {
  tPred= vector(length = length(data)-3)
  for (i in 1:dim(data)[1]) {
    tPred[i] = SecondOrderMarkovChain.predictNextState(transitionTensor, c(data$t2[i],data$t1[i]))
  }
  result = data.frame(data, tPred)
  return (result)
}

# Third order Markov chain
###########################

# Infer transition matrix from windowed state sequence
# in:   - data, windowed observation sequence (names = t3, t2, t1, tNext)
# out:  - transitionTensor containing the transition probabilities of 3rd order markov chain
ThirdOrderMarkovChain.inferTransitionTensor = function(data, numStates) {
  stateSums = list()
  for (i in 1:numStates) {
    stateSums[[i]] = matrix(numStates, nrow=numStates, ncol=numStates) #numStates for sum of 1 (laplace correction)
  }
  transitionTensor = list()
  for (i in 1:numStates) {
    transitionTensor[[i]] = list()
    for (j in 1:numStates) {
      transitionTensor[[i]][[j]] = matrix(1, nrow=numStates, ncol=numStates) #1 for laplace correction
    }
  }
  
  for (i in 1:dim(data)[1]) {
    transitionTensor[[data$tNext[i]]][[data$t3[i]]][data$t2[i],data$t1[i]] =
      transitionTensor[[data$tNext[i]]][[data$t3[i]]][data$t2[i],data$t1[i]] + 1
    stateSums[[data$t3[i]]][data$t2[i],data$t1[i]] = 
      stateSums[[data$t3[i]]][data$t2[i],data$t1[i]] + 1
  }
  for (i in 1:numStates) {
    for (j in 1:numStates) {
      for (k in 1:numStates) {
        for (l in 1:numStates) {
          transitionTensor[[l]][[i]][j,k] = transitionTensor[[l]][[i]][j,k] / stateSums[[i]][j,k]
        }
      }
    }
  }
  return(transitionTensor)
}


# sample markov chain from transition tensor
# beginning from initial states c(state1, state2)
# in:   - transitionTensor, either infered or given transition probabilities of 3rd order markov chain
#       - initialState, a vector containing two integers noting the initial states to begin sampling with
#       - length, length of random walk
# out:  - random walk as state sequence
ThirdOrderMarkovChain.sampleFromTransitionTensor = function(transitionTensor, initialStates, length) {
  numStates = length(transitionTensor)
  samples = vector(length=length)
  samples[1] = initialStates[1]
  samples[2] = initialStates[2]
  samples[3] = initialStates[3]
  transitionVector = vector(length=numStates)
  for (i in 4:length) {
    for (j in 1:numStates) {
      transitionVector[j] = transitionTensor[[j]][[samples[i-3]]][samples[i-2],samples[i-1]]
    }
    samples[i] = sample(1:numStates, size=1, replace=TRUE, prob=transitionVector)
  }
  return(samples)
}

# predict next state given the current and last but current state
# by simply chosing the one with the highest transition probability
# in:   - transitionTensor, either infered or given transition probabilities of 3rd order markov chain
#       - currentState, a vector containing two integers noting the current and last but current 
#         states to predict from
# out:  - next state (integer) with highest transition probability
ThirdOrderMarkovChain.predictNextState = function(transitionTensor, currentStates) {
  numStates = length(transitionTensor)
  transitionVector = vector(length=numStates)
  for (i in 1:numStates) {
    transitionVector[i] = transitionTensor[[i]][[currentStates[1]]][currentStates[2],currentStates[3]]
  }
  return(which.max(transitionVector))
}

# apply prediction to windowed test data
# in:   - transitionTensor, either infered or given transition probabilities of 3rd order markov chain
#       - data, windowed test data (only t3, t2 and t1 are used)
# out:  - windowed data with added column "tPred"
ThirdOrderMarkovChain.predictStates = function(transitionTensor, data) {
  tPred= vector(length = length(data)-3)
  for (i in 1:dim(data)[1]) {
    tPred[i] = ThirdOrderMarkovChain.predictNextState(transitionTensor, c(data$t3[i], data$t2[i], data$t1[i]))
  }
  result = data.frame(data, tPred)
  return (result)
}

# Hidden Markov Model
######################

HiddenMarkovModel.infer = function(data, numStates) {
  cellIds = c(data$t3[1], data$t2[1], data$t1[1], data$tNext)
  uniqueCellIds = 1:numStates
  hmmStates = 1:10
  
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

HiddenMarkovModel.predictStates = function(model, data) {
  tPred = vector(length = length(data)-3)
  for (i in 1:dim(data)[1]) {
    tPred[i] = HiddenMarkovModel.predictNextState(model, data$t1[i])
  }
  result = data.frame(data, tPred)
  return (result)
}