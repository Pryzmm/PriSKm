package com.pryzmm.priSKm.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.bukkit.block.data.type.PointedDripstone;

public class DripstoneThicknessType {

    private final PointedDripstone.Thickness thickness;

    public DripstoneThicknessType(PointedDripstone.Thickness thickness) {
        this.thickness = thickness;
    }

    public String getName() {
        return thickness.name().toLowerCase().replace('_', ' ');
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DripstoneThicknessType that = (DripstoneThicknessType) obj;
        return thickness == that.thickness;
    }

    @Override
    public int hashCode() {
        return thickness != null ? thickness.hashCode() : 0;
    }

    static {
        Classes.registerClass(new ClassInfo<>(DripstoneThicknessType.class, "dripstonethickness")
                .parser(new Parser<DripstoneThicknessType>() {

                    @Override
                    public DripstoneThicknessType parse(String input, ParseContext context) {
                        input = input.toLowerCase().trim().replace(' ', '_');

                        try {
                            PointedDripstone.Thickness thickness = PointedDripstone.Thickness.valueOf(input.toUpperCase());
                            return new DripstoneThicknessType(thickness);
                        } catch (IllegalArgumentException e) {
                            return switch (input) {
                                case "tip_merge", "tipmerge", "merge" ->
                                        new DripstoneThicknessType(PointedDripstone.Thickness.TIP_MERGE);
                                case "tip" -> new DripstoneThicknessType(PointedDripstone.Thickness.TIP);
                                case "frustum" -> new DripstoneThicknessType(PointedDripstone.Thickness.FRUSTUM);
                                case "middle" -> new DripstoneThicknessType(PointedDripstone.Thickness.MIDDLE);
                                case "base" -> new DripstoneThicknessType(PointedDripstone.Thickness.BASE);
                                default -> null;
                            };
                        }
                    }

                    @Override
                    public String toString(DripstoneThicknessType thickness, int flags) {
                        return thickness.toString();
                    }

                    @Override
                    public String toVariableNameString(DripstoneThicknessType thickness) {
                        return thickness.getName().replace(' ', '_');
                    }
                })
        );
    }
}