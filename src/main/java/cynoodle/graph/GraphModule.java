/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
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
