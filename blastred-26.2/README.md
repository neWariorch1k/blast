# Blast Protection Red Leggings — порт на Minecraft 26.2 (Fabric)

Клиентский мод: красит штаны (алмазные / незеритовые и любые другие) в красный цвет,
если на них или на их владельце есть Blast Protection.

## Сборка

Нужно: JDK 25, интернет для Gradle.

1. Gradle wrapper: `gradle wrapper --gradle-version 9.5.1`
   (или скопируйте папку `gradle/` и файлы `gradlew*` из официального fabric-example-mod для 26.x).
2. `./gradlew build`
3. Готовый мод: `build/libs/blastred-1.0.0.jar`

Установка: положить jar в `mods/` вместе с Fabric Loader 0.19+ для Minecraft 26.2.
Fabric API не требуется.

## Что изменилось по сравнению с версией для 1.21.11

* 26.x без обфускации: все `class_xxx` / `method_xxx` заменены на имена Mojang.
  - `ArmorFeatureRenderer#render` -> `HumanoidArmorLayer#submit`
  - `EquipmentRenderer#render` -> `EquipmentLayerRenderer#renderLayers`
  - `OrderedRenderCommandQueue` -> `SubmitNodeCollector`, `getBatchingQueue(order)` -> `order(order)`
  - `RenderLayers.armorCutoutNoCull` -> `RenderTypes.armorCutoutNoCull`
  - `EntityAttributes` -> `Attributes`, `Identifier.of` -> `Identifier.fromNamespaceAndPath`, и т. д.
* Сборка: Loom без remap (`net.fabricmc.fabric-loom`), Java 25, без `mappings` и `modImplementation`.
* `fabric.mod.json`: `minecraft: ~26.2`, `java: >=25`; миксины на `JAVA_25`.
* Логика и текстуры не менялись.
