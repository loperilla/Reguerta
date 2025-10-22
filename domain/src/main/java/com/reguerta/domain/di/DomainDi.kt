package com.reguerta.domain.di

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.containers.ContainersService
import com.reguerta.data.firebase.firestore.measures.MeasuresService
import com.reguerta.data.firebase.firestore.orders.OrdersService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.repository.ConfigRepositoryImpl
import com.reguerta.domain.model.mapper.MeasureMapper
import com.reguerta.domain.model.NewOrderModel
import com.reguerta.domain.time.ClockProvider
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.auth.LoginUseCase
import com.reguerta.domain.usecase.auth.RefreshUserUseCase
import com.reguerta.domain.usecase.auth.RegisterUseCase
import com.reguerta.domain.usecase.auth.SendRecoveryPasswordEmailUseCase
import com.reguerta.domain.usecase.config.UpdateTableTimestampsUseCase
import com.reguerta.domain.usecase.containers.GetAllContainersUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.AddOrderLineUseCase
import com.reguerta.domain.usecase.orderlines.DeleteOrderLineUseCase
import com.reguerta.domain.usecase.orderlines.GetOrderLinesUseCase
import com.reguerta.domain.usecase.orderlines.OrderReceivedModel
import com.reguerta.domain.usecase.orderlines.PushOrderLineToFirebaseUseCase
import com.reguerta.domain.usecase.orderlines.UpdateQuantityOrderLineUseCase
import com.reguerta.domain.usecase.products.AddProductUseCase
import com.reguerta.domain.usecase.products.DeleteProductUseCase
import com.reguerta.domain.usecase.products.EditProductUseCase
import com.reguerta.domain.usecase.products.GetAllProductsByUserIdUseCase
import com.reguerta.domain.usecase.products.GetAvailableProductsUseCase
import com.reguerta.domain.usecase.products.GetProductByIdUseCase
import com.reguerta.domain.usecase.users.AddUserUseCase
import com.reguerta.domain.usecase.users.DeleteUsersUseCase
import com.reguerta.domain.usecase.users.EditUserUseCase
import com.reguerta.domain.usecase.users.GetAllUsersUseCase
import com.reguerta.domain.usecase.users.GetUserByIdUseCase
import com.reguerta.domain.usecase.users.SignOutUseCase
import com.reguerta.domain.usecase.users.ToggleAdminUseCase
import com.reguerta.domain.usecase.users.ToggleProducerUseCase
import com.reguerta.domain.usecase.users.SyncUsersUseCase
import com.reguerta.domain.usecase.app.PreloadCriticalDataUseCase
import com.reguerta.localdata.time.WeekTime
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import com.reguerta.domain.usecase.products.SyncProductsUseCase
import com.reguerta.domain.usecase.containers.SyncContainersUseCase
import com.reguerta.domain.usecase.measures.SyncMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.SyncOrdersAndOrderLinesUseCase
import com.reguerta.localdata.datastore.ReguertaDataStore
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.di
 * Created By Manuel Lopera on 24/2/24 at 10:48
 * All rights reserved 2024
 */

@Module
@InstallIn(ViewModelComponent::class)
object DomainDi {

    @Provides
    fun abstractGetUserUseCase(userService: UsersCollectionService): GetUserByIdUseCase =
        GetUserByIdUseCase(userService)

    @Provides
    fun providesGetAllUsers(userService: UsersCollectionService): GetAllUsersUseCase = GetAllUsersUseCase(userService)

    @Provides
    fun providesAddUser(userService: UsersCollectionService): AddUserUseCase = AddUserUseCase(userService)

    @Provides
    fun providesEditUser(userService: UsersCollectionService): EditUserUseCase = EditUserUseCase(userService)

    @Provides
    fun providesDeleteUser(userService: UsersCollectionService): DeleteUsersUseCase = DeleteUsersUseCase(userService)

    @Provides
    fun providesToggleProducerUseCase(userService: UsersCollectionService) = ToggleProducerUseCase(userService)

    @Provides
    fun providesAdminProducerUseCase(userService: UsersCollectionService) = ToggleAdminUseCase(userService)

    @Provides
    fun providesLoginUseCase(authService: AuthService) = LoginUseCase(authService)

    @Provides
    fun providesRegisterUseCase(authService: AuthService) = RegisterUseCase(authService)

    @Provides
    fun providesRefreshUserUseCase(authService: AuthService) = RefreshUserUseCase(authService)

    @Provides
    fun providesSignOutUseCase(authService: AuthService) = SignOutUseCase(authService)

    @Provides
    fun providesRecoverPasswordUseCase(authService: AuthService) = SendRecoveryPasswordEmailUseCase(authService)

    @Provides
    fun providesCheckAdminProducerUseCase(authService: AuthService) = CheckCurrentUserLoggedUseCase(authService)

    @Provides
    fun providesGetAllProductsByUserIdUseCase(productsService: ProductsService) =
        GetAllProductsByUserIdUseCase(productsService)

    @Provides
    fun providesGetAvailableProductsUseCase(
        productsService: ProductsService,
        usersService: UsersCollectionService,
        weekTime: WeekTime,
        authService: AuthService,
        measuresService: MeasuresService
    ) = GetAvailableProductsUseCase(productsService, usersService, weekTime, authService, measuresService)

    @Provides
    fun providesDeleteProductUseCase(productsService: ProductsService) = DeleteProductUseCase(productsService)

    @Provides
    fun providesAddProductUseCase(productsService: ProductsService) = AddProductUseCase(productsService)

    @Provides
    fun providesGetProductByIdUseCase(productsService: ProductsService) = GetProductByIdUseCase(productsService)

    @Provides
    fun providesEditProductByIdUseCase(productsService: ProductsService) = EditProductUseCase(productsService)

    @Provides
    fun providesAllMeasuresUseCase(measuresService: MeasuresService) = GetAllMeasuresUseCase(measuresService)

    @Provides
    fun providesAllContainersUseCase(containersService: ContainersService) = GetAllContainersUseCase(containersService)

    @Provides
    fun providesGetOrderLinesUseCase(orderLinesService: OrderLinesService) = GetOrderLinesUseCase(orderLinesService)

    @Provides
    fun providesAddOrderLinesUseCase(orderLinesService: OrderLinesService) = AddOrderLineUseCase(orderLinesService)

    @Provides
    fun providesUpdateQuantityOrderLinesUseCase(orderLinesService: OrderLinesService) =
        UpdateQuantityOrderLineUseCase(orderLinesService)

    @Provides
    fun providesDeleteOrderLinesUseCase(orderLinesService: OrderLinesService) =
        DeleteOrderLineUseCase(orderLinesService)

    @Provides
    fun providesPushOrderLinesToFirebaseUseCase(orderLinesService: OrderLinesService) =
        PushOrderLineToFirebaseUseCase(orderLinesService)

    @Provides
    fun providesMeasureMapper(measuresService: MeasuresService) = MeasureMapper(measuresService)

    @Provides
    fun providesOrderLineReceivedModel(
        orderLinesService: OrderLinesService,
        productsService: ProductsService,
        ordersService: OrdersService
    ) = OrderReceivedModel(orderLinesService, productsService, ordersService)

    @Provides
    fun provideNewOrderModel(
        productsService: ProductsService,
        ordersService: OrdersService,
        orderLinesServices: OrderLinesService
    ) = NewOrderModel(productsService, ordersService, orderLinesServices)


    @Provides
    fun provideSyncProductsUseCase(
        productsService: ProductsService,
        dataStore: ReguertaDataStore
    ): SyncProductsUseCase = SyncProductsUseCase(productsService, dataStore)

    @Provides
    fun provideSyncContainersUseCase(
        containersService: ContainersService,
        dataStore: ReguertaDataStore
    ): SyncContainersUseCase = SyncContainersUseCase(containersService, dataStore)

    @Provides
    fun provideSyncMeasuresUseCase(
        measuresService: MeasuresService,
        dataStore: ReguertaDataStore
    ): SyncMeasuresUseCase = SyncMeasuresUseCase(measuresService, dataStore)

    @Provides
    fun provideSyncOrdersAndOrderLinesUseCase(
        ordersService: OrdersService,
        orderLinesService: OrderLinesService,
        dataStore: ReguertaDataStore
    ): SyncOrdersAndOrderLinesUseCase =
        SyncOrdersAndOrderLinesUseCase(ordersService, orderLinesService, dataStore)

    @Provides
    fun provideSyncUsersUseCase(
        usersService: UsersCollectionService,
        dataStore: ReguertaDataStore
    ): SyncUsersUseCase = SyncUsersUseCase(usersService, dataStore)

    @Provides
    fun provideUpdateTableTimestampsUseCase(configRepositoryImpl: ConfigRepositoryImpl): UpdateTableTimestampsUseCase {
        return UpdateTableTimestampsUseCase(configRepositoryImpl)
    }

    @Provides
    fun providePreloadCriticalDataUseCase(
        syncUsersUseCase: SyncUsersUseCase,
        syncProductsUseCase: SyncProductsUseCase,
        syncContainersUseCase: SyncContainersUseCase,
        syncMeasuresUseCase: SyncMeasuresUseCase,
        syncOrdersAndOrderLinesUseCase: SyncOrdersAndOrderLinesUseCase,
    ): PreloadCriticalDataUseCase = PreloadCriticalDataUseCase(
        syncUsersUseCase,
        syncProductsUseCase,
        syncContainersUseCase,
        syncMeasuresUseCase,
        syncOrdersAndOrderLinesUseCase,
    )

    @Provides
    fun provideClockProvider(): ClockProvider = object : ClockProvider {
        override fun today(): LocalDate = LocalDate.now(ZoneId.systemDefault())
        override fun now(): ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
    }

}
