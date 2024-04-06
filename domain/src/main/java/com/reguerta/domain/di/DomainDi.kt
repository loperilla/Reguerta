package com.reguerta.domain.di

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.container.ContainerService
import com.reguerta.data.firebase.firestore.measures.MeasureService
import com.reguerta.data.firebase.firestore.order.OrderServices
import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.model.mapper.MeasureMapper
import com.reguerta.domain.model.new_order.NewOrderModel
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.auth.LoginUseCase
import com.reguerta.domain.usecase.auth.RefreshUserUseCase
import com.reguerta.domain.usecase.auth.RegisterUseCase
import com.reguerta.domain.usecase.container.GetAllContainerUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderline.AddOrderLineUseCase
import com.reguerta.domain.usecase.orderline.DeleteOrderLineUseCase
import com.reguerta.domain.usecase.orderline.GetOrderLinesUseCase
import com.reguerta.domain.usecase.orderline.OrderReceivedModel
import com.reguerta.domain.usecase.orderline.PushOrderLineToFirebaseUseCase
import com.reguerta.domain.usecase.orderline.UpdateQuantityOrderLineUseCase
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
import com.reguerta.domain.usecase.week.GetCurrentWeekDayUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

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
    fun providesGetCurrentDayUseCase(authService: AuthService) = GetCurrentWeekDayUseCase(authService)

    @Provides
    fun providesRegisterUseCase(authService: AuthService) = RegisterUseCase(authService)

    @Provides
    fun providesRefreshUserUseCase(authService: AuthService) = RefreshUserUseCase(authService)

    @Provides
    fun providesSignOutUseCase(authService: AuthService) = SignOutUseCase(authService)

    @Provides
    fun providesCheckAdminProducerUseCase(authService: AuthService) = CheckCurrentUserLoggedUseCase(authService)

    @Provides
    fun providesGetAllProductsByUserIdUseCase(productsService: ProductsService) =
        GetAllProductsByUserIdUseCase(productsService)

    @Provides
    fun providesGetAvailableProductsUseCase(productsService: ProductsService) =
        GetAvailableProductsUseCase(productsService)

    @Provides
    fun providesDeleteProductUseCase(productsService: ProductsService) = DeleteProductUseCase(productsService)

    @Provides
    fun providesAddProductUseCase(productsService: ProductsService) = AddProductUseCase(productsService)

    @Provides
    fun providesGetProductByIdUseCase(productsService: ProductsService) = GetProductByIdUseCase(productsService)

    @Provides
    fun providesEditProductByIdUseCase(productsService: ProductsService) = EditProductUseCase(productsService)

    @Provides
    fun providesAllMeasuresUseCase(measureService: MeasureService) = GetAllMeasuresUseCase(measureService)

    @Provides
    fun providesAllContainerUseCase(containerService: ContainerService) = GetAllContainerUseCase(containerService)

    @Provides
    fun providesGetOrderLinesUseCase(orderLineService: OrderLineService) = GetOrderLinesUseCase(orderLineService)

    @Provides
    fun providesAddOrderLinesUseCase(orderLineService: OrderLineService) = AddOrderLineUseCase(orderLineService)

    @Provides
    fun providesUpdateQuantityOrderLinesUseCase(orderLineService: OrderLineService) =
        UpdateQuantityOrderLineUseCase(orderLineService)

    @Provides
    fun providesDeleteOrderLinesUseCase(orderLineService: OrderLineService) =
        DeleteOrderLineUseCase(orderLineService)

    @Provides
    fun providesPushOrderLinesToFirebaseUseCase(orderLineService: OrderLineService) =
        PushOrderLineToFirebaseUseCase(orderLineService)

    @Provides
    fun providesMeasureMapper(measureService: MeasureService) = MeasureMapper(measureService)

    @Provides
    fun providesOrderLineReceivedModel(
        orderLineService: OrderLineService,
        productsService: ProductsService,
        orderServices: OrderServices
    ) = OrderReceivedModel(orderLineService, productsService, orderServices)

    @Provides
    fun provideNewOrderModel(
        productsService: ProductsService,
        orderServices: OrderServices,
        orderLinesServices: OrderLineService
    ) = NewOrderModel(productsService, orderServices, orderLinesServices)
}
