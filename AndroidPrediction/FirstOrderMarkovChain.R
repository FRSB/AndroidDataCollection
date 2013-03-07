rm(list = ls())

# infer transition matrix from state sequence
# one state per row, states have to be integers beginning from 1
inferTransitionMatrix = function(data) {
	numStates = length(levels(as.factor(data[,1])))
	transitionMatrix = matrix(0, nrow=numStates, ncol=numStates)
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
sampleFromTransitionMatrix = function(transitionMatrix, initialState, length) {
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
predictNextState = function(transitionMatrix, currentState) {
	return(which.max(transitionMatrix[currentState,]))
}

cells = c(1,2,3,4,1,3,4,1,2,4,5)
data = as.data.frame(cells)

transitionMatrix = inferTransitionMatrix(data)
sampleFromTransitionMatrix(transitionMatrix,1,20)
predictNextState(transitionMatrix, 2)



