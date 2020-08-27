package x7.demo.controller;

import io.xream.sqli.api.TemporaryRepository;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.CriteriaBuilder;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.x7.base.web.ViewEntity;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.demo.bean.Cat;
import x7.demo.bean.CatEgg;
import x7.demo.repository.CatRepository;
import x7.demo.service.CatService;

import java.util.List;

@RestController
@RequestMapping("/categg")
public class CatEggController {

    @Autowired
    private TemporaryRepository temporaryRepository;
    @Autowired
    private CatRepository catRepository;
    @Autowired
    private CatService catService;

    @RequestMapping("/test")
    public ViewEntity test(){
//        boolean flag = this.temporaryRepository.createRepository(CatEgg.class);
//        System.out.println(flag);
//
//        CatEgg catEgg = new CatEgg();
//        catEgg.setId(1);
//        catEgg.setDogId(2);
//        catEgg.setName("test");
//
//        this.temporaryRepository.create(catEgg);

        //test....
        CriteriaBuilder.ResultMapBuilder fromBuilder = CriteriaBuilder.resultMapBuilder();
        fromBuilder.resultKey("cat.id","id");
        fromBuilder.resultKey("cat.taxType","name");
        fromBuilder.resultKey("cat.dogId","dog_id");
        fromBuilder.sourceScript("from cat");

        boolean flag = this.temporaryRepository.findToCreate(CatEgg.class, fromBuilder.build());



        CriteriaBuilder.ResultMapBuilder builder = CriteriaBuilder.resultMapBuilder();
        builder.resultWithDottedKey().resultKey("c.id").resultKey("c.type");
        builder.withoutOptimization().sourceScript("FROM cat c inner join catEgg e on e.dogId = c.id");

        Criteria.ResultMapCriteria ResultMapCriteria = builder.build();

        this.catService.findToHandle(ResultMapCriteria, map -> {

            Long id = MapUtils.getLong(map,"c.id");
            String catType = MapUtils.getString(map,"c.type");

            try {
                //service | fallback, not suggest to use transaction
                this.catService.refresh(
                        RefreshCondition.build().refresh("type", "NNLL").eq("id", id)
                );
            }catch (Exception e){

            }

        });

        this.temporaryRepository.dropRepository(CatEgg.class);

        return ViewEntity.ok();
    }


    public void testFindToHanle(){
        CriteriaBuilder builder = CriteriaBuilder.builder(Cat.class);
        builder.eq("id",2);

        Criteria criteria = builder.build();

        this.catService.findToHandleC(criteria, obj -> {

            Long id = obj.getId();
            String catType = obj.getType();
            List<String> list = obj.getTestList();

            try {
                //service | fallback, not suggest to use transaction
                this.catService.refresh(
                        RefreshCondition.build().refresh("type", "NNLL").eq("id", id)
                );
            }catch (Exception e){

            }

        });

    }
}
