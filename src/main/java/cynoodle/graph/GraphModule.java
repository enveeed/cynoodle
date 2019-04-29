/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.graph;

import cynoodle.module.MIdentifier;
import cynoodle.module.MSystem;
import cynoodle.module.Module;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.*;

import java.util.function.Consumer;

/**
 * Provides the {@link graphql.GraphQL GraphQL} API.
 */
@MIdentifier("graph")
@MSystem
public final class GraphModule extends Module {
    private GraphModule() {}

    // ===

    /**
     * The GraphQL instance.
     */
    private GraphQL graph = null;

    // ===

    @Override
    protected void start() {
        super.start();


        GraphQLObjectType makeMeObject = GraphQLObjectType.newObject()
                .name("MakeMe")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("foo")
                        .type(Scalars.GraphQLString)
                ).build();

        GraphQLCodeRegistry registry = GraphQLCodeRegistry.newCodeRegistry()
                .dataFetcher(FieldCoordinates.coordinates("MakeMe", "foo"), new DataFetcher<Object>() {
                    @Override
                    public Object get(DataFetchingEnvironment environment) throws Exception {
                        return environment.getField().getName();
                    }
                }).build();


        getManager().getRegistry()
                .all()
                .filter(module -> module instanceof GraphProvider)
                .forEach(new Consumer<Module>() {
                    @Override
                    public void accept(Module module) {

                    }
                });
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}
