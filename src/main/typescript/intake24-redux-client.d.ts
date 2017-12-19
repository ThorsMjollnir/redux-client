import {Store} from "redux";

export class Client {
    constructor(reduxStore: Store<any>, stateSelector: string[]);

    init(apiBaseUrl: string);

    setRefreshToken(token: string);

    signin(email: string, password: string);

    refreshAccessToken();
}

export class ClientReducer {

    static create();
}

export interface FoodHeader {
    code: string;
    localDescription: string;
}

export interface CategoryHeader {
    code: string;
    localDescription: string;
}

export interface LookupResult {
    foods: FoodHeader[],
    categories: CategoryHeader[];
}

export interface FoodSearchState {
    searchPending: boolean;
    result: LookupResult;
}

export class FoodSearch {
    constructor(reduxStore: Store<any>, apiClient: Client, stateSelector: string[]);
    search(query: string);
    getState(): FoodSearchState;
}

export class FoodSearchReducer {
    static create();
}
