import {Store} from "redux";

export interface ClientState {
    apiBaseUrl?: string;
    refreshToken?: string;
    accessToken?: string;
    signinRequestPending: boolean;
    errors: string[];
}

export class Client {
    constructor(reduxStore: Store<any>, stateSelector: string[]);

    setApiBaseUrl(apiBaseUrl: string);

    setRefreshToken(token: string);

    signin(email: string, password: string);

    refreshAccessToken();

    getState(): ClientState;
}

export class ClientReducer {

    static create();
}

export class FNCReducer {

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
    requestPending: boolean;
    searchResult: LookupResult;
}

export class FoodSearch {
    constructor(reduxStore: Store<any>, apiClient: Client, stateSelector: string[]);

    search(query: string);

    select(foodCode: string);

    getState(): FoodSearchState;
}

export class FoodSearchReducer {
    static create();
}

export interface PortionSizeMethod {
    method: string;
    description: string;
    imageUrl: string;
    useForRecipes: boolean;
    parameters: object
}

export interface MethodSelectorState {
    availableMethods: PortionSizeMethod[],
    selectedIndex?: number
}

export interface MethodSelector {
    selectMethod(index: number): void;

    getState(): MethodSelectorState;
}

export interface FoodNutrientsCalculatorState {
    currentPrompt: { type: string }
}

export class FoodNutrientsCalculator {
    constructor(reduxStore: Store<any>, apiClient: Client, stateSelector: string[]);

    foodSearch: FoodSearch;

    methodSelector: MethodSelector;

    getState(): FoodNutrientsCalculatorState;
}
