package org.podborochka.common.graphql

import com.google.genai.errors.ClientException
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.podborochka.common.exceptions.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Component

@Component
class GlobalGraphQlExceptionResolver : DataFetcherExceptionResolverAdapter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun resolveToSingleError(
        ex: Throwable,
        env: DataFetchingEnvironment
    ): GraphQLError {
        log.error("GraphQL error on field {}", env.executionStepInfo.path, ex)

        return when (ex) {
            is IllegalArgumentException -> GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.message ?: "Bad request")
                .build()

            is ResourceNotFoundException -> GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.message ?: "Not found")
                .build()

            is IllegalStateException -> GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ex.message ?: "Illegal state")
                .build()
            is ClientException -> GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.message ?: "Illegal state")
                .build()

            else -> GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("Unexpected server error: ${ex.message}")
                .build()
        }
    }
}