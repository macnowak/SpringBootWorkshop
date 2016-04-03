package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory
import pl.decerto.workshop.HolidayRequestService

class requestCounterCommand {

	@Usage("count requests")
	@Command
	def main(InvocationContext context) {
		BeanFactory beans = context.attributes['spring.beanfactory']
		def requestEndpoint = beans.getBean(HolidayRequestService)
		return requestEndpoint.getRequestCount();
	}

}