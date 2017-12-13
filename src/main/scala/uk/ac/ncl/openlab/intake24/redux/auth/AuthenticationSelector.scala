package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.redux.{Selector, Store}

class AuthenticationSelector(val store: Store, val namespace: String) extends Selector[AuthenticationState]