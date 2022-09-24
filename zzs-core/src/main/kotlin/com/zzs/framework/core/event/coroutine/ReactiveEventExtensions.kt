package com.zzs.framework.core.event

import kotlinx.coroutines.reactor.awaitSingleOrNull

suspend fun ReactiveEventPublisher.publishAndAwait(suppliers: Collection<EventSupplier>) {
  this.publish(suppliers).awaitSingleOrNull()
}

suspend fun ReactiveEventPublisher.publishAndAwait(suppliers: EventSuppliers) {
  this.publish(suppliers).awaitSingleOrNull()
}

suspend fun ReactiveEventPublisher.publishAndAwait(supplier: EventSupplier) {
  this.publish(supplier).awaitSingleOrNull()
}
