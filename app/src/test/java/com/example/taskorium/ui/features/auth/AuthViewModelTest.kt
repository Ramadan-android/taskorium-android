package com.example.taskorium.ui.features.auth

import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.domain.model.User
import com.example.taskorium.domain.repository.AuthRepository
import com.example.taskorium.domain.repository.CategoryRepository
import com.example.taskorium.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher(){
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
//@OptIn(ExperimentalCoroutinesApi::class)
//class MainDispatcherRule(
//    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
//) : TestWatcher() {
//    override fun starting(description: Description) {
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    override fun finished(description: Description) {
//        Dispatchers.resetMain()
//    }
//}

//@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @JvmField
    @Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel
    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val taskRepository = mockk<TaskRepository>(relaxed = true)
    private val categoryRepository = mockk<CategoryRepository>(relaxed = true)

    @Before
    fun setUp() {
        viewModel = AuthViewModel(authRepository, taskRepository, categoryRepository)
    }

    @Test
    fun `EmailChanged event updates state`(){
        // given
        val email = "test@example.com"
        // when
        viewModel.onEvent(AuthUiEvent.EmailChanged(email))
        // then
        assertEquals(email, viewModel.state.value.email)
    }

    @Test
    fun `PasswordChanged event updates state`(){
        // given
        val password = "password123"
        // when
        viewModel.onEvent(AuthUiEvent.PasswordChanged(password))
        // then
        assertEquals(password, viewModel.state.value.password)
    }

    @Test
    fun `ToggleScreenMode change login to register and back`(){
        // given
        // check first state is login
        assertEquals(AuthScreenMode.Login, viewModel.state.value.screenMode)
        assertEquals(AuthScreenMode.Login.name, viewModel.state.value.authButtonText)

        // when
        viewModel.onEvent(AuthUiEvent.ToggleScreenMode)

        // then
        assertEquals(AuthScreenMode.Register, viewModel.state.value.screenMode)
        assertEquals(AuthScreenMode.Register.name, viewModel.state.value.authButtonText)

        // Toggle back to Login
        viewModel.onEvent(AuthUiEvent.ToggleScreenMode)
        assertEquals(AuthScreenMode.Login, viewModel.state.value.screenMode)
        assertEquals("Login", viewModel.state.value.authButtonText)
        assertEquals("Register", viewModel.state.value.toggleButtonText)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `LoginClicked calls repository and updates success state`() = runTest {
        val user = mockk<User>()
        coEvery { authRepository.login(any(),any()) } returns NetworkResult.Success(user)
        coEvery { taskRepository.fetchTasks() } returns NetworkResult.Success("Success")
        coEvery { categoryRepository.fetchCategories() } returns NetworkResult.Success("Success")

        val effect = mutableListOf<AuthUiEffectEvent>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.effectEvent.collect {
                effect.add(it)
            }
        }

        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
        viewModel.onEvent(AuthUiEvent.LoginClicked)

        coVerify { authRepository.login("test@example.com", "password") }
        coVerify { taskRepository.fetchTasks() }
        coVerify { categoryRepository.fetchCategories() }

        assertEquals(true, viewModel.state.value.isSuccess)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(listOf(AuthUiEffectEvent.NavigateToHome), effect)

        job.cancel()
    }

    @Test
    fun `LoginClicked calls repository and updates error state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns NetworkResult.Error("Invalid credentials")

        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
        viewModel.onEvent(AuthUiEvent.LoginClicked)

        coVerify { authRepository.login("test@example.com", "password") }

        assertEquals("Invalid credentials", viewModel.state.value.errorMessage)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isSuccess)

    }

    @Test
    fun `LoginClicked calls repository and updates loading state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns NetworkResult.Loading

        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
        viewModel.onEvent(AuthUiEvent.LoginClicked)

        coVerify { authRepository.login("test@example.com", "password") }

        assertEquals(true, viewModel.state.value.isLoading)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `RegisterClicked calls repository and updates success state`() = runTest {
        val user = mockk<User>()
        coEvery { authRepository.register(any(), any()) } returns NetworkResult.Success(user)
        coEvery { taskRepository.fetchTasks() } returns NetworkResult.Success("Success")
        coEvery { categoryRepository.fetchCategories() } returns NetworkResult.Success("Success")

        val effect = mutableListOf<AuthUiEffectEvent>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.effectEvent.collect {
                effect.add(it)
            }
        }

        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
        viewModel.onEvent(AuthUiEvent.RegisterClicked)

        coVerify { authRepository.register("test@example.com", "password") }
        coVerify { taskRepository.fetchTasks() }
        coVerify { categoryRepository.fetchCategories() }

        assertEquals(true, viewModel.state.value.isSuccess)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(listOf(AuthUiEffectEvent.NavigateToHome), effect)

        job.cancel()
    }

    @Test
    fun `RegisterClicked calls repository and updates error state`() = runTest {
        coEvery { authRepository.register(any(), any()) } returns NetworkResult.Error("Email already exists")

        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
        viewModel.onEvent(AuthUiEvent.RegisterClicked)

        coVerify { authRepository.register("test@example.com", "password") }

        assertEquals(false, viewModel.state.value.isSuccess)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals("Email already exists", viewModel.state.value.errorMessage)
    }


}



//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//
//    private lateinit var viewModel: AuthViewModel
//    private val authRepository = mockk<AuthRepository>(relaxed = true)
//    private val taskRepository = mockk<TaskRepository>(relaxed = true)
//    private val catRepository = mockk<CategoryRepository>(relaxed = true)
//
//    @Before
//    fun setUp() {
//        viewModel = AuthViewModel(authRepository, taskRepository, catRepository)
//    }
//
//    @Test
//    fun `initial state is correct`() {
//        val state = viewModel.state.value
//        assertEquals("", state.email)
//        assertEquals("", state.password)
//        assertEquals(AuthScreenMode.Login, state.screenMode)
//        assertEquals(false, state.isLoading)
//        assertEquals(false, state.isSuccess)
//    }
//
//    @Test
//    fun `EmailChanged event updates state`() {
//        val email = "test@example.com"
//        viewModel.onEvent(AuthUiEvent.EmailChanged(email))
//        assertEquals(email, viewModel.state.value.email)
//    }
//
//    @Test
//    fun `PasswordChanged event updates state`() {
//        val password = "password123"
//        viewModel.onEvent(AuthUiEvent.PasswordChanged(password))
//        assertEquals(password, viewModel.state.value.password)
//    }
//
//    @Test
//    fun `ToggleScreenMode changes Login to Register and back`() {
//        // Starts as Login
//        assertEquals(AuthScreenMode.Login, viewModel.state.value.screenMode)
//
//        // Toggle to Register
//        viewModel.onEvent(AuthUiEvent.ToggleScreenMode)
//        assertEquals(AuthScreenMode.Register, viewModel.state.value.screenMode)
//        assertEquals("Register", viewModel.state.value.authButtonText)
//        assertEquals("Login", viewModel.state.value.toggleButtonText)
//
//        // Toggle back to Login
//        viewModel.onEvent(AuthUiEvent.ToggleScreenMode)
//        assertEquals(AuthScreenMode.Login, viewModel.state.value.screenMode)
//        assertEquals("Login", viewModel.state.value.authButtonText)
//        assertEquals("Register", viewModel.state.value.toggleButtonText)
//    }
//
//    @Test
//    fun `LoginClicked calls repository and updates success state`() = runTest {
//        val user = mockk<User>()
//        coEvery { authRepository.login(any(), any()) } returns NetworkResult.Success(user)
//        coEvery { taskRepository.fetchTasks() } returns NetworkResult.Success("Success")
//        coEvery { catRepository.fetchCategories() } returns NetworkResult.Success("Success")
//
//        val effects = mutableListOf<AuthUiEffectEvent>()
//        val job = launch(UnconfinedTestDispatcher()) {
//            viewModel.effectEvent.collect { effects.add(it) }
//        }
//
//        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
//        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
//        viewModel.onEvent(AuthUiEvent.LoginClicked)
//
//        coVerify { authRepository.login("test@example.com", "password") }
//        coVerify { taskRepository.fetchTasks() }
//        coVerify { catRepository.fetchCategories() }
//
//        assertEquals(true, viewModel.state.value.isSuccess)
//        assertEquals(false, viewModel.state.value.isLoading)
//        assertEquals(listOf(AuthUiEffectEvent.NavigateToHome), effects)
//
//        job.cancel()
//    }
//
//    @Test
//    fun `LoginClicked handles error`() = runTest {
//        val errorMessage = "Invalid credentials"
//        coEvery { authRepository.login(any(), any()) } returns NetworkResult.Error(errorMessage)
//
//        viewModel.onEvent(AuthUiEvent.LoginClicked)
//
//        assertEquals(errorMessage, viewModel.state.value.errorMessage)
//        assertEquals(false, viewModel.state.value.isLoading)
//        assertEquals(false, viewModel.state.value.isSuccess)
//    }
//
//    @Test
//    fun `RegisterClicked calls repository and updates success state`() = runTest {
//        val user = mockk<User>()
//        coEvery { authRepository.register(any(), any()) } returns NetworkResult.Success(user)
//        coEvery { taskRepository.fetchTasks() } returns NetworkResult.Success("Success")
//        coEvery { catRepository.fetchCategories() } returns NetworkResult.Success("Success")
//
//        val effects = mutableListOf<AuthUiEffectEvent>()
//        val job = launch(UnconfinedTestDispatcher()) {
//            viewModel.effectEvent.collect { effects.add(it) }
//        }
//
//        viewModel.onEvent(AuthUiEvent.EmailChanged("test@example.com"))
//        viewModel.onEvent(AuthUiEvent.PasswordChanged("password"))
//        viewModel.onEvent(AuthUiEvent.RegisterClicked)
//
//        coVerify { authRepository.register("test@example.com", "password") }
//        coVerify { taskRepository.fetchTasks() }
//        coVerify { catRepository.fetchCategories() }
//
//        assertEquals(true, viewModel.state.value.isSuccess)
//        assertEquals(false, viewModel.state.value.isLoading)
//        assertEquals(listOf(AuthUiEffectEvent.NavigateToHome), effects)
//
//        job.cancel()
//    }
//
//    @Test
//    fun `RegisterClicked handles error`() = runTest {
//        val errorMessage = "Email already exists"
//        coEvery { authRepository.register(any(), any()) } returns NetworkResult.Error(errorMessage)
//
//        viewModel.onEvent(AuthUiEvent.RegisterClicked)
//
//        assertEquals(errorMessage, viewModel.state.value.errorMessage)
//        assertEquals(false, viewModel.state.value.isLoading)
//        assertEquals(false, viewModel.state.value.isSuccess)
//    }
//
//    @Test
//    fun `NavigateToHome emits NavigateToHome effect`() = runTest {
//        val effects = mutableListOf<AuthUiEffectEvent>()
//        val job = launch(UnconfinedTestDispatcher()) {
//            viewModel.effectEvent.collect { effects.add(it) }
//        }
//
//        viewModel.NavigateToHome()
//
//        assertEquals(listOf(AuthUiEffectEvent.NavigateToHome), effects)
//        job.cancel()
//    }