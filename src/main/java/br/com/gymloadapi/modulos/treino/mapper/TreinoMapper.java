package br.com.gymloadapi.modulos.treino.mapper;

import br.com.gymloadapi.modulos.exercicio.mapper.ExercicioMapper;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.treino.dto.TreinoRequest;
import br.com.gymloadapi.modulos.treino.dto.TreinoResponse;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(imports = LocalDate.class, uses = ExercicioMapper.class)
public interface TreinoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "request.nome")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "exercicios", source = "exercicios")
    @Mapping(target = "dataCadastro", expression = "java(LocalDate.now())")
    Treino mapToModel(TreinoRequest request, Usuario usuario, List<Exercicio> exercicios);

    TreinoResponse mapToResponse(Treino treino);
}
